package com.ivan_degtev.documentaccounting2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.documentaccounting2.model.interfaces.Authorable;
import com.ivan_degtev.documentaccounting2.model.interfaces.Available;
import com.ivan_degtev.documentaccounting2.model.interfaces.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ForeignKey;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = { "data" })
public class FileEntity implements BaseEntity, Authorable, Available {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;
    private String filetype;

//    @Lob
    @Column(columnDefinition = "BYTEA")
    private byte[] data;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonProperty("author")
    @JoinColumn(name = "author_id")
    private User author;

    @JsonProperty(value = "public_file_entity", defaultValue = "false")
    private Boolean publicEntity = false;

    @JsonProperty(value = "available_for")
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "file_entity_user_access",
            joinColumns = @JoinColumn(name = "id_file_entity"),
            inverseJoinColumns = @JoinColumn(name = "id_user",
                    foreignKey = @ForeignKey(name = "fk_file_entity_user",
                            foreignKeyDefinition = "FOREIGN KEY (id_user) REFERENCES users(id_user) ON DELETE CASCADE"))
    )
    @JsonIgnore
    private Set<User> availableFor = new HashSet<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDate creationDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDate updateDate;
}
