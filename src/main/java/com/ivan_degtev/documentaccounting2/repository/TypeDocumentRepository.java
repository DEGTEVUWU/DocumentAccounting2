package com.ivan_degtev.documentaccounting2.repository;

import com.ivan_degtev.documentaccounting2.model.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeDocumentRepository extends JpaRepository<TypeDocument, Long> {
    Optional<TypeDocument> findById(Long id);
}
