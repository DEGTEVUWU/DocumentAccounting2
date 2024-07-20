package com.ivan_degtev.documentaccounting2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.documentaccounting2.model.interfaces.Authorable;
import com.ivan_degtev.documentaccounting2.model.interfaces.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileEntity implements BaseEntity, Authorable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String filetype;

    @Lob
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty("author")
    @JoinColumn(name = "author_id")
    private User author;
}
