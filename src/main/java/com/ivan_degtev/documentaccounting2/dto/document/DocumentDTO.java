package com.ivan_degtev.documentaccounting2.dto.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.documentaccounting2.model.TypeDocument;
import com.ivan_degtev.documentaccounting2.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@ToString
public class DocumentDTO {
    private Long id;
    private String title;
    private Long number;
    private User author;
    private String content;
    private TypeDocument type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate creationDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate updateDate;
    @JsonProperty("public_document")
    private Boolean publicDocument;
    @JsonProperty("available_for")
    private Set<Long> availableFor;
}