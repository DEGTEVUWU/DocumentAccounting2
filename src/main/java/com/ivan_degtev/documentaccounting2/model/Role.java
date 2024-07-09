package com.ivan_degtev.documentaccounting2.model;

import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "roles", schema = "public")
@ToString
public class Role implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRole;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleEnum name;

}