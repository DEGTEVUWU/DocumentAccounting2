package com.ivan_degtev.documentaccounting2.repository;

import com.ivan_degtev.documentaccounting2.model.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    @Query("""
            SELECT fe FROM FileEntity fe
            LEFT JOIN fe.availableFor faf
            WHERE fe.publicEntity = true
            OR fe.author.idUser = :userId
            OR faf.idUser = :userId
            GROUP BY fe.id
            """)
    List<FileEntity> findAllByAuthorIdUserAndPublicFile(@Param("userId") Long userId);

    @Query("""
            SELECT fe FROM FileEntity fe
            LEFT JOIN fe.availableFor faf
            WHERE (:fileNameCont IS NULL OR :fileNameCont = '' OR fe.filename LIKE %:fileNameCont%)
            AND (:authorCont IS NULL OR :authorCont = '' OR fe.author.username LIKE %:authorCont%)
            AND (:fileTypeCont IS NULL OR :fileTypeCont = '' OR fe.filetype LIKE %:fileTypeCont%)
            AND (:creationDate IS NULL OR TO_CHAR(fe.creationDate, 'yyyy-MM-dd') = :creationDate)
            AND (fe.publicEntity = true OR fe.author.idUser = :userId OR faf.idUser = :userId)
            GROUP BY fe.id
            """)
    Page<FileEntity> searchFiles(
            @Param("fileNameCont") String fileNameCont,
            @Param("authorCont") String authorCont,
            @Param("fileTypeCont") String fileTypeCont,
            @Param("creationDate") String creationDate,
            @Param("userId") Long userId,
            Pageable pageable
    );

}
