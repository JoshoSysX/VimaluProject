package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.dto.ProductoDTO;
import com.web.proyect.hacienda_vimalu.entity.Producto;
import com.web.proyect.hacienda_vimalu.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements IProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodo() {

        return productoRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductoDTO> buscarPorId(Long id) {

        return productoRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    @Transactional
    public ProductoDTO crear(ProductoDTO p) {

        Producto producto = new Producto();

        producto.setNombre(p.nombre());
        producto.setDescripcion(p.descripcion());
        producto.setCategoria(p.categoria());
        producto.setPrecio(p.precio());
        producto.setStock(p.stock());
        producto.setImagen(p.imagen());

        return convertToDTO(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public Optional<ProductoDTO> actualizar(Long id, ProductoDTO p) {

        return productoRepository.findById(id)
                .map(producto -> {

                    producto.setNombre(p.nombre());
                    producto.setDescripcion(p.descripcion());
                    producto.setCategoria(p.categoria());
                    producto.setPrecio(p.precio());
                    producto.setStock(p.stock());
                    producto.setImagen(p.imagen());

                    return convertToDTO(
                            productoRepository.save(producto)
                    );
                });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {

        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }

        return false;
    }

    private ProductoDTO convertToDTO(Producto p) {

        return new ProductoDTO(
                p.getIdProducto(),
                p.getNombre(),
                p.getDescripcion(),
                p.getCategoria(),
                p.getPrecio(),
                p.getStock(),
                p.getImagen()
        );
    }
}
