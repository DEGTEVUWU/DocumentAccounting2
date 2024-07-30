package com.ivan_degtev.documentaccounting2.controller;

import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityParamsDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityUpdateDTO;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import com.ivan_degtev.documentaccounting2.service.impl.FileServiceImpl;
import com.ivan_degtev.documentaccounting2.service.other.HeaderConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/files")
@AllArgsConstructor
public class FileController {

    private final HttpHeaders HttpHeadersPNG;
    private final FileServiceImpl fileService;
    private final HeaderConfig.HeaderService headerService;

    @GetMapping(path = "")
    public ResponseEntity<List<FileEntityDTO>> getAllFiles() {
        List<FileEntityDTO> fileEntityDTOS =  fileService.getAll();
        return ResponseEntity.ok().body(fileEntityDTOS);
    }

    @GetMapping(path = "for_user")
    public ResponseEntity<List<FileEntityDTO>> getAllFilesForUsers() {
        List<FileEntityDTO> fileEntityDTOS = fileService.getAllForUsers();
        return ResponseEntity.ok().body(fileEntityDTOS);
    }

    @GetMapping(path = "/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<FileEntityDTO>> search(
            @ModelAttribute FileEntityParamsDTO fileEntityParamsDTO,
            @RequestParam(defaultValue = "1") int pageNumber
    ) {
        Page<FileEntityDTO> fileEntityDTOS = fileService.searchFiles(fileEntityParamsDTO, pageNumber);
        return ResponseEntity.ok()
                .body(fileEntityDTOS);
    }

    /*
     делает миниатюру файла,
     используется на страницах с инфой о файле и на общей странице
     */
    @GetMapping(path = "/{id}/thumbnail")
    public ResponseEntity<byte[]> getFileThumbnail(@PathVariable Long id) {
        byte[] thumbnailData = fileService.getThumbnailFile(id);
        return ResponseEntity.ok()
                .headers(HttpHeadersPNG)
                .body(thumbnailData);
    }


    @PostMapping("/upload")
    public FileEntity uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestPart("params") String paramsJson
    ) throws IOException
    {
        return fileService.storeFile(file, paramsJson);
    }

    // используется на странице с информацией о текущем файле
    @GetMapping(path = "show_data_file/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userUtils.currentUserIsAuthorForFiles(#id)" +
            "or @userUtils.currentFileEntityIsPublicOrAvailableForFileEntity(#id)")
    public ResponseEntity<FileEntityDTO> showDataFile(@PathVariable Long id) {
        FileEntityDTO fileEntityDTO = fileService.getDataFile(id);
        return ResponseEntity.ok()
                .body(fileEntityDTO);
    }


    // используется для просмотра во весь экран в браузере или скачивания файла(зависит от типа файла)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userUtils.currentUserIsAuthorForFiles(#id)" +
            "or @userUtils.currentFileEntityIsPublicOrAvailableForFileEntity(#id)")
    public ResponseEntity<byte[]> showFile(
            @PathVariable Long id,
            @RequestParam(required = false) boolean download
    )
            throws UnsupportedEncodingException
    {
        FileEntity fileEntity = fileService.getFile(id);
        HttpHeaders httpHeader = headerService.headersToInteractFile(fileEntity, download);
        return ResponseEntity.ok()
                .headers(httpHeader)
                .body(fileEntity.getData());
    }

    @PutMapping(path = "{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userUtils.currentUserIsAuthorForFiles(#id)")
    public ResponseEntity<FileEntityDTO> updateFile(
            @RequestBody FileEntityUpdateDTO fileEntityUpdateDTO,
            @PathVariable Long id
    ) {
        FileEntityDTO fileEntityDTO = fileService.update(fileEntityUpdateDTO, id);
        return ResponseEntity.ok().body(fileEntityDTO);
    }


    @DeleteMapping(path = "{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) throws FileNotFoundException {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}

