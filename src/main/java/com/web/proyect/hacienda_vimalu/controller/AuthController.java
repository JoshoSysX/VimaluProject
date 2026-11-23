package com.web.proyect.hacienda_vimalu.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.web.proyect.hacienda_vimalu.dto.AuthResponse;
import com.web.proyect.hacienda_vimalu.dto.LoginRequest;
import com.web.proyect.hacienda_vimalu.dto.RegistroDTO;
import com.web.proyect.hacienda_vimalu.dto.UsuarioDTO;
import com.web.proyect.hacienda_vimalu.entity.Usuario;
import com.web.proyect.hacienda_vimalu.repository.UsuarioRepository;
import com.web.proyect.hacienda_vimalu.security.JwtService;
import com.web.proyect.hacienda_vimalu.security.UsuarioPrincipal;
import com.web.proyect.hacienda_vimalu.services.IUsuarioService;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {
    private final IUsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthController(IUsuarioService usuarioService, AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody RegistroDTO dto){
        return usuarioService.registrarCliente(dto)
                .map(user-> new ResponseEntity<>(user, HttpStatus.CREATED))
                .orElseThrow( ()-> new RuntimeException("El email o nombre del usuario ya estan en uso"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usuario(),request.password())
        );
        Usuario user = usuarioRepository.findByUsuario(request.usuario()).orElseThrow();
        String jwtToken = jwtService.generateToken(new UsuarioPrincipal(user));
        return ResponseEntity.ok(new AuthResponse(
                jwtToken,
                user.getUsuario(),
                user.getRol().name(),
                user.getPersona().getIdPersona()

        ));


    }
}
