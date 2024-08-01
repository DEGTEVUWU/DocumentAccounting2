package com.ivan_degtev.documentaccounting2.model;

import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import com.ivan_degtev.documentaccounting2.model.interfaces.BaseEntity;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;

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

    @CreatedDate
    @Column(updatable = false)
    private LocalDate creationDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDate updateDate;
}