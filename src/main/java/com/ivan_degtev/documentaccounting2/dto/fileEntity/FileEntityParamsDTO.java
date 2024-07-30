package com.ivan_degtev.documentaccounting2.dto.fileEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class FileEntityParamsDTO {
    private String fileNameCont;
    private String authorCont;
    private String fileTypeCont;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    private Boolean publicDocument = true;

    private String sortBy = "filename";
    private String sortDirection = "ASC";
}
