package com.ivan_degtev.documentaccounting2.dto.document;

import com.ivan_degtev.documentaccounting2.utils.UserUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

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

    // Для сортировки
    private String sortBy; // поле, по которому нужно сортировать ('title', 'author', ...)
    private String sortDirection; // направление ('asc' или 'desc')
}