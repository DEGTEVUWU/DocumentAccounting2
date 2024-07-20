package com.ivan_degtev.documentaccounting2.service;

import com.ivan_degtev.documentaccounting2.model.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    FileEntity storeFile(MultipartFile file) throws IOException;
    FileEntity getFile(Long id);
}
