package com.ivan_degtev.documentaccounting2.service.impl;

import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityParamsDTO;
import com.ivan_degtev.documentaccounting2.exceptions.NotFoundException;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import com.ivan_degtev.documentaccounting2.model.User;
import com.ivan_degtev.documentaccounting2.repository.FileRepository;
import com.ivan_degtev.documentaccounting2.repository.UserRepository;
import com.ivan_degtev.documentaccounting2.service.FileService;
import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private UserUtils userUtils;
    private FileRepository fileRepository;
    private final UserRepository userRepository;

    @Override
    public List<FileEntity> findAll() {
        log.info("зашёл в сервисный метод получить все файлы ");
        return fileRepository.findAll();
    }
    @Override
    public FileEntity storeFile(MultipartFile file, FileEntityParamsDTO paramsDTO) throws IOException {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFilename(file.getOriginalFilename());
        fileEntity.setFiletype(file.getContentType());
        fileEntity.setData(file.getBytes());
        fileEntity.setAuthor(userUtils.getCurrentUser());

        if (paramsDTO != null && paramsDTO.getPublicEntity()) {
            fileEntity.setPublicEntity(true);
        }
        if (paramsDTO != null && paramsDTO.getAvailableFor() != null && !paramsDTO.getAvailableFor().isEmpty()) {
            fileEntity.setAvailableFor(mappingFromDtoToEntity(paramsDTO.getAvailableFor()));
        }
        return fileRepository.save(fileEntity);
    }
    private Set<User> mappingFromDtoToEntity(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(userRepository.findAllById(userIds));
    }
    //маппер в классе для теста, мб позже перенесу его в маппер, но пока здесь для наглядности


    @Override
    public FileEntity getFile(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new NotFoundException("File not found with id " + id));
    }
    @Override
    public void deleteFile(Long id) {
        fileRepository.deleteById(id);
    }
}
