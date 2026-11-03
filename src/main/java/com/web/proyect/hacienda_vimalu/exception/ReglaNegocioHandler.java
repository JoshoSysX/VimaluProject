package com.web.proyect.hacienda_vimalu.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class ReglaNegocioHandler {
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> manejar(ReglaNegocioException ex) {
        return ResponseEntity.status(409).body(Map.of("mensaje", ex.getMessage()));
    }
}