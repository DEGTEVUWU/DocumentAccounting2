package com.ivan_degtev.documentaccounting2.controller;

import com.ivan_degtev.documentaccounting2.model.FileEntity;
import com.ivan_degtev.documentaccounting2.service.impl.FileServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileServiceImpl fileService;

    @PostMapping("/upload")
    public FileEntity uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        return fileService.storeFile(file);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userUtils.currentUserIsAuthorForFiles(#id)")
    public ResponseEntity<byte[]> showFile(@PathVariable Long id, @RequestParam(required = false) boolean download)
            throws UnsupportedEncodingException {
        FileEntity fileEntity = fileService.getFile(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileEntity.getFiletype()));

        String dispositionType = download ? "attachment" : "inline";
        String encodedFilename = URLEncoder.encode(fileEntity.getFilename(), StandardCharsets.UTF_8).replace("+", "%20");
        String contentDisposition = String.format("%s; filename=\"%s\"", dispositionType, encodedFilename);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileEntity.getData());
    }
    @DeleteMapping(path = "{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) throws FileNotFoundException {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}

