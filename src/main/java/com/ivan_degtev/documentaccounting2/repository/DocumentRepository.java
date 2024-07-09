package com.ivan_degtev.documentaccounting2.repository;

import com.ivan_degtev.documentaccounting2.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    boolean existsDocumentByTitle(String title);
    List<Document> findAllByAuthorIdUser(Long authorId);
    Optional<Document> findDocumentById(Long documentId);
}
