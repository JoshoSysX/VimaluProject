package com.web.proyect.hacienda_vimalu.services;

import com.web.proyect.hacienda_vimalu.exception.ReglaNegocioException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements IFileStorageService {
    private final Path directory;

    public FileStorageServiceImpl(
            @Value("${storage.location:images}") String location) {
        directory = Path.of(location).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(directory);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo crear la carpeta de imágenes: " + directory, ex);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty() || file.getSize() > 1024 * 1024) {
            throw new ReglaNegocioException("Seleccione una imagen de hasta 1 MB");
        }
        try {
            byte[] bytes = file.getBytes();
            String extension;
            if (bytes.length >= 8 && bytes[0] == (byte) 0x89 && bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
                extension = ".png";
            } else if (bytes.length >= 3 && bytes[0] == (byte) 0xff && bytes[1] == (byte) 0xd8 && bytes[2] == (byte) 0xff) {
                extension = ".jpg";
            } else if (bytes.length >= 6 && bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
                extension = ".gif";
            } else {
                throw new ReglaNegocioException("La imagen debe ser PNG, JPG o GIF");
            }
            try (var input = file.getInputStream()) {
                if (ImageIO.read(input) == null) throw new ReglaNegocioException("El archivo no es una imagen válida");
            }
            // El nombre original no se usa como ruta del archivo.
            String filename = UUID.randomUUID() + extension;
            Files.createDirectories(directory);
            Path destination = directory.resolve(filename);
            try {
                Files.write(destination, bytes);
            } catch (IOException ex) {
                Files.deleteIfExists(destination);
                throw ex;
            }
            return filename;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar la imagen", ex);
        }
    }

    @Override
    public void delete(String filename) {
        if (filename == null || filename.isBlank()) return;
        // Tambien acepta la URL relativa devuelta al consultar el producto.
        String name = filename.substring(filename.lastIndexOf('/') + 1);
        if (!name.matches("[a-fA-F0-9-]{36}\\.(png|jpg|gif)")) return;
        Path file = directory.resolve(name).normalize();
        if (!file.getParent().equals(directory)) return;
        try {
            Files.deleteIfExists(file);
        } catch (IOException ex) {
            // La operacion en BD ya termino; no se devuelve un error falso al cliente.
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("No se pudo eliminar la imagen {}", name, ex);
        }
    }
}
