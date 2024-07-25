package com.ivan_degtev.documentaccounting2.repository;


import com.ivan_degtev.documentaccounting2.model.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    @Query(value = " SELECT fe.* FROM file_entity fe " +
            " LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
            " WHERE fe.public_entity = true " +
            " OR fe.author_id = :userId " +
            " OR fua.id_user = :userId " +
            " GROUP BY fe.id ", nativeQuery = true)
    List<FileEntity> findAllByAuthorIdUserAndPublicFile(@Param("userId") Long userId);

//    @Query(value = "SELECT * FROM file_entity fe " +
//            "LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
//            "WHERE (:fileNameCont IS NULL OR fe.filename LIKE %:fileNameCont%) " +
//            "AND (:authorCont IS NULL OR fe.author_id IN (SELECT id FROM users WHERE username LIKE %:authorCont%)) " +
//            "AND (:fileTypeCont IS NULL OR fe.filetype LIKE %:fileTypeCont%) " +
//            "AND (:creationDate IS NULL OR fe.creation_date = :creationDate) " +
//            "AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId)",
//            countQuery = "SELECT COUNT(*) FROM file_entity fe " +
//                    "LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
//                    "WHERE (:fileNameCont IS NULL OR fe.filename LIKE %:fileNameCont%) " +
//                    "AND (:authorCont IS NULL OR fe.author_id IN (SELECT id FROM users WHERE username LIKE %:authorCont%)) " +
//                    "AND (:fileTypeCont IS NULL OR fe.filetype LIKE %:fileTypeCont%) " +
//                    "AND (:creationDate IS NULL OR fe.creation_date = :creationDate) " +
//                    "AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId)",
//            nativeQuery = true)

//    @Query(value = "SELECT * FROM file_entity fe " +
//            "LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
//            "WHERE (:fileNameCont IS NULL OR fe.filename LIKE %:fileNameCont%) " +
//            "AND (:authorCont IS NULL OR fe.author_id IN (SELECT id FROM users WHERE username LIKE %:authorCont%)) " +
//            "AND (:fileTypeCont IS NULL OR fe.filetype LIKE %:fileTypeCont%) " +
//            "AND (:creationDate IS NULL OR fe.creation_date = :creationDate) " +
//            "AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId)",
//            countQuery = "SELECT COUNT(*) FROM file_entity fe " +
//                    "LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
//                    "WHERE (:fileNameCont IS NULL OR fe.filename LIKE %:fileNameCont%) " +
//                    "AND (:authorCont IS NULL OR fe.author_id IN (SELECT id FROM users WHERE username LIKE %:authorCont%)) " +
//                    "AND (:fileTypeCont IS NULL OR fe.filetype LIKE %:fileTypeCont%) " +
//                    "AND (:creationDate IS NULL OR fe.creation_date = :creationDate) " +
//                    "AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId)",
//            nativeQuery = true)

//    @Query(value = " SELECT * FROM file_entity fe " +
//            " LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
//            " WHERE (:fileNameCont IS NULL OR :fileNameCont = '' OR fe.filename LIKE %:fileNameCont%) " +
//            " AND (:authorCont IS NULL OR :authorCont = '' OR fe.author_id IN (SELECT id_user FROM users WHERE username LIKE %:authorCont%)) " +
//            " AND (:fileTypeCont IS NULL OR :fileTypeCont = '' OR fe.filetype LIKE %:fileTypeCont%) " +
//            " AND (:creationDate IS NULL OR fe.creation_date = :creationDate) " +
//            " AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId) ",
//            countQuery = " SELECT COUNT(*) FROM file_entity fe " +
//                    " LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
//                    " WHERE (:fileNameCont IS NULL OR :fileNameCont = '' OR fe.filename LIKE %:fileNameCont%) " +
//                    " AND (:authorCont IS NULL OR :authorCont = '' OR fe.author_id IN (SELECT id_user FROM users WHERE username LIKE %:authorCont%)) " +
//                    " AND (:fileTypeCont IS NULL OR :fileTypeCont = '' OR fe.filetype LIKE %:fileTypeCont%) " +
//                    " AND (:creationDate IS NULL OR fe.creation_date = :creationDate) " +
//                    " AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId) ",
//            nativeQuery = true)

//    @Query(value = " SELECT fe.* FROM file_entity fe " +
//            " LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
//            " WHERE (:fileNameCont IS NULL OR :fileNameCont = '' OR fe.filename LIKE %:fileNameCont%) " +
//            " AND (:authorCont IS NULL OR :authorCont = '' OR fe.author_id IN (SELECT u.id_user FROM users u WHERE u.username LIKE %:authorCont%)) " +
//            " AND (:fileTypeCont IS NULL OR :fileTypeCont = '' OR fe.filetype LIKE %:fileTypeCont%) " +
//            " AND (:creationDate IS NULL OR fe.creation_date = :creationDate) " +
//            " AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId) ",
//            countQuery = " SELECT COUNT(fe.*) FROM file_entity fe " +
//                    " LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
//                    " WHERE (:fileNameCont IS NULL OR :fileNameCont = '' OR fe.filename LIKE %:fileNameCont%) " +
//                    " AND (:authorCont IS NULL OR :authorCont = '' OR fe.author_id IN (SELECT u.id_user FROM users u WHERE u.username LIKE %:authorCont%)) " +
//                    " AND (:fileTypeCont IS NULL OR :fileTypeCont = '' OR fe.filetype LIKE %:fileTypeCont%) " +
//                    " AND (:creationDate IS NULL OR fe.creation_date = :creationDate) " +
//                    " AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId) ",
//            nativeQuery = true)

//    @Query(value = " SELECT * FROM file_entity fe " +
//            " LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
//            " WHERE (:fileNameCont IS NULL OR :fileNameCont = '' OR fe.filename LIKE %:fileNameCont%) " +
//            " AND (:authorCont IS NULL OR :authorCont = '' OR fe.author_id IN " +
//            " (SELECT u.id_user FROM users u WHERE u.username LIKE %:authorCont%)) " +
//            " AND (:fileTypeCont IS NULL OR :fileTypeCont = '' OR fe.filetype LIKE %:fileTypeCont%) " +
//            " AND (:creationDate IS NULL OR fe.creation_date = CAST(:creationDate AS date)) " +
//            " AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId) ",
//            countQuery = " SELECT COUNT(*) FROM file_entity fe " +
//                    " LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
//                    " WHERE (:fileNameCont IS NULL OR :fileNameCont = '' OR fe.filename LIKE %:fileNameCont%) " +
//                    " AND (:authorCont IS NULL OR :authorCont = '' OR fe.author_id IN " +
//                    " (SELECT u.id_user FROM users u WHERE u.username LIKE %:authorCont%)) " +
//                    " AND (:fileTypeCont IS NULL OR :fileTypeCont = '' OR fe.filetype LIKE %:fileTypeCont%) " +
//                    " AND (:creationDate IS NULL OR fe.creation_date = CAST(:creationDate AS date)) " +
//                    " AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId) ",
//            nativeQuery = true)
    //эта работает полностью, но без даты


    @Query(value = " SELECT fe.* FROM file_entity fe " +
            " LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
            " WHERE (:fileNameCont IS NULL OR :fileNameCont = '' OR fe.filename LIKE %:fileNameCont%) " +
            " AND (:authorCont IS NULL OR :authorCont = '' OR fe.author_id IN (SELECT u.id_user FROM users u WHERE u.username LIKE %:authorCont%)) " +
            " AND (:fileTypeCont IS NULL OR :fileTypeCont = '' OR fe.filetype LIKE %:fileTypeCont%) " +
            " AND (:creationDate IS NULL OR TO_CHAR(fe.creation_date, 'yyyy-MM-dd') = :creationDate) " +
            " AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId) ",
            countQuery = " SELECT COUNT(*) FROM file_entity fe " +
                    " LEFT JOIN file_entity_user_access fua ON fe.id = fua.id_file_entity " +
                    " WHERE (:fileNameCont IS NULL OR :fileNameCont = '' OR fe.filename LIKE %:fileNameCont%) " +
                    " AND (:authorCont IS NULL OR :authorCont = '' OR fe.author_id IN (SELECT u.id_user FROM users u WHERE u.username LIKE %:authorCont%)) " +
                    " AND (:fileTypeCont IS NULL OR :fileTypeCont = '' OR fe.filetype LIKE %:fileTypeCont%) " +
                    " AND (:creationDate IS NULL OR TO_CHAR(fe.creation_date, 'yyyy-MM-dd') = :creationDate) " +
                    " AND (fe.public_entity = true OR fe.author_id = :userId OR fua.id_user = :userId) ",
            nativeQuery = true)
    Page<FileEntity> searchFiles(@Param("fileNameCont") String fileNameCont,
                                 @Param("authorCont") String authorCont,
                                 @Param("fileTypeCont") String fileTypeCont,
                                 @Param("creationDate") String creationDate, // Измените тип на String
                                 @Param("userId") Long userId,
                                 Pageable pageable);
}
