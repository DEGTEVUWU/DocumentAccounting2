package com.ivan_degtev.documentaccounting2.service;

import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityParamsDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityUpdateDTO;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface FileService {
    List<FileEntityDTO> getAll();
    List<FileEntityDTO> getAllForUsers(Long userId);
//    List<FileEntity> findAll();
    Page<FileEntityDTO> searchFiles(
            FileEntityParamsDTO params,
            int pageNumber,
            String sortBy,
            String sortDirection
    );
    FileEntity storeFile(MultipartFile file, FileEntityUpdateDTO paramsDTO) throws IOException;
    FileEntity getFile(Long id);
    FileEntityDTO getDataFile(Long id);
    FileEntityDTO update(FileEntityUpdateDTO fileEntityUpdateDTO, Long id);
    void deleteFile(Long id);
    byte[] generateThumbnail(byte[] fileData, String fileType);
}
