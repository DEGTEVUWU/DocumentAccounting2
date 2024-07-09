package com.ivan_degtev.documentaccounting2.model;

import com.ivan_degtev.documentaccounting2.model.enums.TypeDocumentEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = { "author", "type" })
public class Document implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String title;

    @NotNull
    private Long number;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
    private User author;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE }, fetch = FetchType.EAGER)
    private TypeDocument type;

    @CreatedDate
    private LocalDate creationDate;

    @UpdateTimestamp
    private LocalDate updateDate;
}
