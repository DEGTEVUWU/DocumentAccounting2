package com.ivan_degtev.documentaccounting2.repository;


import com.ivan_degtev.documentaccounting2.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
}
