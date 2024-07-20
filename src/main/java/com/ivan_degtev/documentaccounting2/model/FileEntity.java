package com.ivan_degtev.documentaccounting2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan_degtev.documentaccounting2.model.interfaces.Authorable;
import com.ivan_degtev.documentaccounting2.model.interfaces.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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
}
