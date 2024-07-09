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

    @DateTimeFormat(pattern = "yyyy-MM-dd") // Указываем формат даты
    private LocalDate creationDate;

    // Для сортировки
    private String sortBy; // поле, по которому нужно сортировать ('title', 'author', ...)
    private String sortDirection; // направление ('asc' или 'desc')
}