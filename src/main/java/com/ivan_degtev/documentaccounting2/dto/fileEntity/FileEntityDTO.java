package com.ivan_degtev.documentaccounting2.dto.fileEntity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@ToString(exclude = { "data" })
public class FileEntityDTO {
    private Long id;
    private String filename;
    private String filetype;
    private String author;

    @JsonIgnore
    private byte[] data;
    private Boolean publicEntity;

    @JsonProperty("available_for")
    private Set<Long> availableFor;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate creationDate;
    private LocalDate updateDate;
}

