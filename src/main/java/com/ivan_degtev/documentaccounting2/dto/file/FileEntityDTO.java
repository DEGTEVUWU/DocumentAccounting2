package com.ivan_degtev.documentaccounting2.dto.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class FileEntityDTO {
    private Long id;
    private String filename;
    private String filetype;
    private String author;

    @JsonIgnore
    private byte[] data;
    private Boolean publicEntity;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate creationDate;
    private LocalDate updateDate;
}

