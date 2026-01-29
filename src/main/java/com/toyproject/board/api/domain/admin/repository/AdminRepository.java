package com.toyproject.board.api.domain.admin.repository;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.querydsl.AdminRepositoryCustom;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long>, AdminRepositoryCustom {
    Boolean existsByUserId(String userId);

    Optional<Admin> findByUserId(String userId);
}
