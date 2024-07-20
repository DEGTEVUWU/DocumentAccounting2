package com.ivan_degtev.documentaccounting2.controller;

import com.ivan_degtev.documentaccounting2.model.FileEntity;
import com.ivan_degtev.documentaccounting2.service.impl.FileServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    //    @GetMapping("/downloads/{id}")
//    public ResponseEntity<byte[]> downloadsFile(@PathVariable Long id) {
//        FileEntity fileEntity = fileService.getFile(id);
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(fileEntity.getFiletype()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getFilename() + "\"")
//                .body(fileEntity.getData());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> showFile(@PathVariable Long id, @RequestParam(required = false) boolean download)
            throws UnsupportedEncodingException {
        FileEntity fileEntity = fileService.getFile(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileEntity.getFiletype()));

        // Correctly set Content-Disposition header
        String dispositionType = download ? "attachment" : "inline";
        String encodedFilename = URLEncoder.encode(fileEntity.getFilename(), StandardCharsets.UTF_8).replace("+", "%20");
        String contentDisposition = String.format("%s; filename=\"%s\"", dispositionType, encodedFilename);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileEntity.getData());
    }
}

