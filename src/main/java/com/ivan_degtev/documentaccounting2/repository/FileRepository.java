package com.ivan_degtev.documentaccounting2.repository;


import com.ivan_degtev.documentaccounting2.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
}
