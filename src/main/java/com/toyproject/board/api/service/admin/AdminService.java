package com.toyproject.board.api.service.admin;

import com.toyproject.board.api.domain.admin.repository.AdminRepository;
import com.toyproject.board.api.dto.admin.AdminDto;
import com.toyproject.board.api.dto.admin.request.AdminSearchAdminReq;
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

    public Page<AdminDto> searchAdmin(AdminSearchAdminReq req, Pageable pageable) {
        return adminRepository.findAllByCondition(req.getName(), req.getPhone(), req.getEmail(), pageable).map(AdminDto::from);
    }

}
