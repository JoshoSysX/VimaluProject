package com.web.proyect.hacienda_vimalu.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
@Service
public class JwtService {
    private final Key signingKey;
    @Value("${security.jwt.expiration-time:120000}")
    private long jwtExpiration;

    public JwtService(@Value("${security.jwt.secret-key:}") String secretKey) {
        // Para uso local sin clave configurada, los tokens vencen al reiniciar.
        signingKey = secretKey.isBlank() ? Keys.secretKeyFor(SignatureAlgorithm.HS256)
                : Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role",userDetails.getAuthorities().iterator().next().getAuthority());
        return generateToken(extraClaims,userDetails);
    }

    public String generateToken(Map<String, Object> extraClaim, UserDetails userDetails){
        return buildToken(extraClaim,userDetails,jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims,
                              UserDetails userDetails, long expiration){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSingInKey(), SignatureAlgorithm.HS256)
                .compact();

    }


    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    private boolean isTokenExpired(String token){
        return  extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSingInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private Key getSingInKey(){
        return signingKey;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims=extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String extractUsername(String token){
        return extractClaim(token,Claims::getSubject);
    }
}
