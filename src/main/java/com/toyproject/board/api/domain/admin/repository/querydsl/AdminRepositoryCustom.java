package com.toyproject.board.api.domain.admin.repository.querydsl;

import com.toyproject.board.api.domain.admin.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminRepositoryCustom {
    Page<Admin> findAllByCondition(String name, String phone, String email, Pageable pageable);
}
