package com.toyproject.board.api.service.authorization;

import com.sun.jdi.request.DuplicateRequestException;
import com.toyproject.board.api.config.properties.JwtTokenProperty;
import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.AdminRepository;
import com.toyproject.board.api.dto.admin.AdminLoginDto;
import com.toyproject.board.api.dto.admin.request.AdminCreateReq;
import com.toyproject.board.api.dto.admin.request.AdminLoginReq;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthorizationService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProperty jwtTokenProperty;

    public AdminLoginDto loginAdmin(AdminLoginReq req) {
        Admin admin = adminRepository.findByUserId(req.userId())
                .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(req.password(), admin.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProperty.createToken(admin.getUserId());
        return AdminLoginDto.from(admin.getIdx(),admin.getUserId(),token);
    }

    @Transactional
    public void createAdmin(AdminCreateReq req) {

        // 아이디 중복 체크
        boolean isDuplicated = adminRepository.existsByUserId(req.userId());

        if (isDuplicated) {
            throw new DuplicateRequestException("이미 사용 중인 아이디입니다");
        }

        // 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(req.password());

        Admin admin = req.toEntity();
        admin.updatePassword(encodePassword);

        // 아이디 저장
        adminRepository.save(admin);
    }

}
