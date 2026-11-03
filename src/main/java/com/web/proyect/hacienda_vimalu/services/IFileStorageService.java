package com.web.proyect.hacienda_vimalu.services;

import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {
    String store(MultipartFile file);
    void delete(String filename);
}