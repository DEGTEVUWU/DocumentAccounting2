package com.ivan_degtev.documentaccounting2.dto.document;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.documentaccounting2.annotation.ValidUser;
import com.ivan_degtev.documentaccounting2.model.TypeDocument;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
@ToString
public class UpdateDocumentDTO {
    @NotNull
    private JsonNullable<String> title;
    @NotNull
    private JsonNullable<Long> number;

    @ValidUser
    @JsonProperty("author_id")
    @NotNull
    private JsonNullable<Long> authorId;
    @NotNull
    private JsonNullable<String> content;

    @JsonProperty("type_id")
    @NotNull
    private JsonNullable<Long> typeId;
    @JsonProperty("public_document")
    private Boolean publicDocument;
    @JsonProperty("available_for")
    private JsonNullable<Set<Long>> availableFor;
}
