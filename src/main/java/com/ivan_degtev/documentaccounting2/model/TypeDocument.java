package com.ivan_degtev.documentaccounting2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ivan_degtev.documentaccounting2.model.enums.TypeDocumentEnum;
import com.ivan_degtev.documentaccounting2.model.interfaces.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "type_document", schema = "public")
@ToString
public class TypeDocument implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TypeDocumentEnum type;

    @OneToMany
    @JoinColumn(name = "type_id")
    @JsonIgnore
    private Set<Document> documents = new HashSet<>();
}
