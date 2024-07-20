package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import com.ivan_degtev.documentaccounting2.repository.FileRepository;
import com.ivan_degtev.documentaccounting2.service.FileService;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private UserUtils userUtils;
    private FileRepository fileRepository;

    @Override
    public FileEntity storeFile(MultipartFile file) throws IOException {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFilename(file.getOriginalFilename());
        fileEntity.setFiletype(file.getContentType());
        fileEntity.setData(file.getBytes());
        fileEntity.setAuthor(userUtils.getCurrentUser());

        return fileRepository.save(fileEntity);
    }

    @Override
    public FileEntity getFile(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new NotFoundException("File not found with id " + id));
    }
    @Override
    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }
}
