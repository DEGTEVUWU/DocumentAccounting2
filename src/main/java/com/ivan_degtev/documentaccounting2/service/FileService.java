package com.ivan_degtev.documentaccounting2.service;

import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityParamsDTO;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface FileService {
    FileEntity storeFile(MultipartFile file, FileEntityParamsDTO paramsDTO) throws IOException;
    FileEntity getFile(Long id);
    void deleteFile(Long id);
}
