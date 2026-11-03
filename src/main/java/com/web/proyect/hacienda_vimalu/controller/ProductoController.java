package com.web.proyect.hacienda_vimalu.controller;

import com.web.proyect.hacienda_vimalu.dto.ProductoDTO;
import com.web.proyect.hacienda_vimalu.exception.ResourceNotFoundException;
import com.web.proyect.hacienda_vimalu.services.IFileStorageService;
import com.web.proyect.hacienda_vimalu.services.IProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springdoc.core.annotations.ParameterObject;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final IProductoService productoService;
    private final IFileStorageService storageService;

    public ProductoController(IProductoService productoService, IFileStorageService storageService) {
        this.productoService = productoService;
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarTodo() {
        return ResponseEntity.ok(productoService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(obtener(id));
    }

    // Se conserva JSON para los clientes que no envian un archivo.
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(conImagen(dto, null)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoDTO> crearConImagen(
            @Valid @ModelAttribute @ParameterObject ProductoDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        String imagen = guardarArchivo(file);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(conImagen(dto, imagen)));
        } catch (RuntimeException ex) {
            storageService.delete(imagen);
            throw ex;
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        return actualizarProducto(id, dto, null);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoDTO> actualizarConImagen(@PathVariable Long id,
            @Valid @ModelAttribute @ParameterObject ProductoDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return actualizarProducto(id, dto, file);
    }

    private ResponseEntity<ProductoDTO> actualizarProducto(Long id, ProductoDTO dto, MultipartFile file) {
        ProductoDTO anterior = obtener(id);
        String nueva = guardarArchivo(file);
        try {
            ProductoDTO actualizado = productoService.actualizar(id, conImagen(dto, nueva))
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
            // El servicio ya confirmo la actualizacion en BD antes de borrar la anterior.
            if (nueva != null && !Objects.equals(anterior.imagen(), actualizado.imagen())) {
                storageService.delete(anterior.imagen());
            }
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException ex) {
            storageService.delete(nueva);
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ProductoDTO anterior = obtener(id);
        if (!productoService.eliminar(id)) throw new ResourceNotFoundException("Producto no encontrado");
        storageService.delete(anterior.imagen());
        return ResponseEntity.noContent().build();
    }

    private ProductoDTO obtener(Long id) {
        return productoService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
    }

    private String guardarArchivo(MultipartFile file) {
        return file == null || file.isEmpty() ? null : storageService.store(file);
    }

    private ProductoDTO conImagen(ProductoDTO dto, String imagen) {
        return new ProductoDTO(null, dto.nombre(), dto.descripcion(), dto.categoria(),
                dto.precio(), dto.stock(), imagen);
    }
}