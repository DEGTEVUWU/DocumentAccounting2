package com.ivan_degtev.documentaccounting2.repository;

import com.ivan_degtev.documentaccounting2.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    boolean existsDocumentByTitle(String title);

    @Query(value = " SELECT * FROM documents d " +
            " WHERE d.public_document = true " +
            "OR d.author_id_user = :authorId ", nativeQuery = true)
    List<Document> findAllByAuthorIdUserAndPublicDocument(@Param("authorId") Long authorId);
    Optional<Document> findDocumentById(Long documentId);
}
