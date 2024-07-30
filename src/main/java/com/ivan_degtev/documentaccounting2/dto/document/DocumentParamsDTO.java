package com.ivan_degtev.documentaccounting2.dto.document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class DocumentParamsDTO {

    private String titleCont;
    private Long number;
    private String authorCont;
    private String contentCont;
    private String typeCont;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    private Boolean publicDocument = true;

    private String sortBy;
    private String sortDirection;
}