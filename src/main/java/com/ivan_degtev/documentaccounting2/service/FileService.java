package com.ivan_degtev.documentaccounting2.service;

import com.ivan_degtev.documentaccounting2.dto.file.FileEntityDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityParamsDTO;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface FileService {
    List<FileEntityDTO> getAll();
    List<FileEntity> findAll();
    FileEntity storeFile(MultipartFile file, FileEntityParamsDTO paramsDTO) throws IOException;
    FileEntity getFile(Long id);
    FileEntityDTO getDataFile(Long id);
    FileEntityDTO update(FileEntityParamsDTO fileEntityUpdateDTO, Long id);
    void deleteFile(Long id);
    byte[] generateThumbnail(byte[] fileData, String fileType);
}
