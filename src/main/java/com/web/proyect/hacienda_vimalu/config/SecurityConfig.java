package com.web.proyect.hacienda_vimalu.config;

import com.web.proyect.hacienda_vimalu.security.JwtAuthenticationFilter;
import com.web.proyect.hacienda_vimalu.exception.CustomAccessDeniedHandler;
import com.web.proyect.hacienda_vimalu.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter,
            AuthenticationProvider provider, CustomAccessDeniedHandler denied,
            CustomAuthenticationEntryPoint unauthorized) throws Exception {
        return http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(provider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/registro", "/api/v1/auth/login", "/images/**",
                                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
                        .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/salones/**", "/api/mesas/**").permitAll()
                        .requestMatchers("/api/productos/**", "/api/salones/**", "/api/mesas/**", "/api/pagos/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/**").hasAnyAuthority("ADMIN", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/reservas").hasAnyAuthority("ADMIN", "CLIENTE")
                        .requestMatchers("/api/reservas/**").hasAuthority("ADMIN")
                        .anyRequest().denyAll())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(errors -> errors.authenticationEntryPoint(unauthorized).accessDeniedHandler(denied))
                .build();
    }
}
