package com.ivan_degtev.documentaccounting2.repository;

import com.ivan_degtev.documentaccounting2.model.Role;
import com.ivan_degtev.documentaccounting2.model.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    //    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Optional<Role> findByName(RoleEnum name);
}