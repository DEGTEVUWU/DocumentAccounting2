package com.ivan_degtev.documentaccounting2.dto.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.documentaccounting2.annotation.ValidUser;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
@ToString
public class CreateDocumentDTO {
    private String title;
    private Long number;
    @ValidUser
    @JsonProperty("author_id")
    private Long authorId;
    private String content;
    @JsonProperty("type_id")
    private Long typeId;
    @JsonProperty(value = "public_document", defaultValue = "false")
    private Boolean publicDocument = false; //по умолчанию, при отсутсвии поля в ДТО
    @JsonProperty("available_for")
    private Set<Long> availableFor;
}
