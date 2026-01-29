package com.toyproject.board.api.service.admin;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.AdminRepository;
import com.toyproject.board.api.dto.admin.AdminDto;
import com.toyproject.board.api.dto.admin.request.AdminCreateReq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    public Page<AdminDto> searchAdmin(String name, String phone, String email, Pageable pageable) {
        return adminRepository.findAllByCondition(name, phone, email, pageable).map(AdminDto::from);
    }

    @Transactional
    public void createAdmin(AdminCreateReq req) {

        // 아이디 중복 체크
        Boolean b = adminRepository.existsByUserId(req.userId());

        // 비밀번호 암호화

        // 아이디 저장
        adminRepository.save(req.toEntity());
    }
}
