package com.web.proyect.hacienda_vimalu.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Object> error(int status, String message) {
        return ResponseEntity.status(status).body(Map.of("timestamp", LocalDateTime.now(),
                "status", status, "message", message));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> noEncontrado(ResourceNotFoundException ex) { return error(404, ex.getMessage()); }
    @ExceptionHandler({DuplicateResourceException.class, InsufficientStockException.class})
    public ResponseEntity<Object> conflicto(RuntimeException ex) { return error(409, ex.getMessage()); }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> autenticacion(AuthenticationException ex) { return error(401, "Usuario o contraseña incorrectos"); }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> permisos(AccessDeniedException ex) { return error(403, "No tienes permiso para acceder a este recurso"); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("timestamp", LocalDateTime.now(), "status", 400, "errors", errors));
    }
}
