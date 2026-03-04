package com.toyproject.board.api.service.authorization;

import com.sun.jdi.request.DuplicateRequestException;
import com.toyproject.board.api.config.exception.ClientException;
import com.toyproject.board.api.config.properties.JwtTokenProperty;
import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.AdminRepository;
import com.toyproject.board.api.domain.member.repository.MemberRepository;
import com.toyproject.board.api.dto.admin.AdminLoginDto;
import com.toyproject.board.api.dto.auth.request.AdminCreateReq;
import com.toyproject.board.api.dto.auth.request.AdminLoginReq;
import com.toyproject.board.api.dto.auth.response.TokenRes;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.jwt.RefreshToken;
import com.toyproject.board.api.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProperty jwtTokenProperty;

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;


    public AdminLoginDto loginAdmin(AdminLoginReq req) {
        // 관리자 조회
        Admin admin = adminRepository.findByUserId(req.userId())
                .orElseThrow(() -> new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다."));
        // 비밀번호 검증
        if (!passwordEncoder.matches(req.password(), admin.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        // 토큰 생성
        String accessToken = jwtTokenProperty.createToken(admin.getIdx(), admin.getRoleType());
        String refreshToken = jwtTokenProperty.createRefreshToken(admin.getIdx(), admin.getRoleType());

        // Redis 리프레쉬 토큰 저장
        refreshTokenRepository.save(RefreshToken.of(admin.getIdx(), admin.getRoleType(), refreshToken));

        return AdminLoginDto.from(admin.getIdx(),admin.getUserId(),accessToken, refreshToken);
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

    @Transactional
    public TokenRes reissue(String oldRefreshToken) {
        if (!jwtTokenProperty.validateToken(oldRefreshToken)) {
            throw new ClientException(ExceptionType.UNAUTHORIZED, "리프레시 토큰이 만료되었거나 유효하지 않습니다");
        }

        RefreshToken savedToken = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> new ClientException(ExceptionType.UNAUTHORIZED, "토큰 정보를 찾을 수 없습니다"));

        refreshTokenRepository.delete(savedToken);

        Long userIdx = savedToken.getUserIdx();
        RoleType roleType = savedToken.getRoleType();

        String newAccessToken = jwtTokenProperty.createToken(userIdx, roleType);
        String newRefreshToken = jwtTokenProperty.createRefreshToken(userIdx, roleType);

        return TokenRes.from(newAccessToken, newRefreshToken);
    }
}
