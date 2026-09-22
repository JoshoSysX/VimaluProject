package com.web.proyect.hacienda_vimalu.controller;

import com.web.proyect.hacienda_vimalu.dto.ProductoDTO;
import com.web.proyect.hacienda_vimalu.services.IProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final IProductoService productoService;

    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarTodo() {
        return ResponseEntity.ok(
                productoService.listarTodo()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(
            @PathVariable Long id) {

        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(
            @Valid @RequestBody ProductoDTO producto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                productoService.crear(producto)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO producto) {

        return productoService.actualizar(id, producto)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        if (productoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}
