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

    @Query("""
            SELECT d FROM Document d
            LEFT JOIN d.availableFor daf
            WHERE d.publicDocument = true
            OR d.author.idUser = :authorId
            OR daf.idUser = :authorId
            ORDER BY d.id ASC
            """)
    List<Document> findAllByAuthorIdUserAndPublicDocument(@Param("authorId") Long authorId);
    Optional<Document> findDocumentById(Long documentId);
}
