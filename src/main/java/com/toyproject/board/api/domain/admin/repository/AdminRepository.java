package com.toyproject.board.api.domain.admin.repository;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.querydsl.AdminRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long>, AdminRepositoryCustom {
}
