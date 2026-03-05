package com.toyproject.board.api.service.authorization;

import com.toyproject.board.api.exception.ClientException;
import com.toyproject.board.api.jwt.properties.JwtTokenProperty;
import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.AdminRepository;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.member.repository.MemberRepository;
import com.toyproject.board.api.dto.admin.AdminLoginDto;
import com.toyproject.board.api.dto.auth.TokenDto;
import com.toyproject.board.api.dto.auth.request.AdminCreateReq;
import com.toyproject.board.api.dto.auth.request.AdminLoginReq;
import com.toyproject.board.api.dto.auth.request.MemberLoginReq;
import com.toyproject.board.api.dto.member.MemberLoginDto;
import com.toyproject.board.api.dto.member.request.MemberCreateReq;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.jwt.RefreshToken;
import com.toyproject.board.api.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

import static com.toyproject.board.api.constants.RedisConstants.LOGOUT;
import static com.toyproject.board.api.constants.RedisConstants.LOGOUT_ACCESS_TOKEN;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProperty jwtTokenProperty;

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public AdminLoginDto loginAdmin(AdminLoginReq req) {
        // 관리자 조회
        Admin admin = adminRepository.findByUserId(req.userId())
                .orElseThrow(() -> new ClientException(ExceptionType.UNAUTHORIZED_LOGIN_FAIL_ADMIN));
        // 비밀번호 검증
        validatePasswordMatch(req.password(), admin.getPassword(), admin.getRoleType());
        // 토큰 생성
        TokenDto tokenDto = tokenCreateAndSave(admin.getIdx(), admin.getRoleType());
        return AdminLoginDto.from(admin.getIdx(), admin.getUserId(), tokenDto);
    }

    @Transactional
    public MemberLoginDto loginMember(MemberLoginReq req) {
        // 멤버 조회
        Member member = memberRepository.findByEmail(req.email())
                .orElseThrow(() -> new ClientException(ExceptionType.UNAUTHORIZED_LOGIN_FAIL_MEMBER));
        // 비밀번호 검증
        validatePasswordMatch(req.password(), member.getPassword(), member.getRoleType());
        TokenDto tokenDto = tokenCreateAndSave(member.getIdx(), member.getRoleType());
        return MemberLoginDto.from(member.getIdx(), tokenDto);
    }

    @Transactional
    public void createAdmin(AdminCreateReq req) {

        passwordCheck(req.password(), req.passwordCheck());
        // 아이디 중복 체크
        boolean isDuplicated = adminRepository.existsByUserId(req.userId());

        if (isDuplicated) {
            throw new ClientException(ExceptionType.CONFLICT_CREATE_DUPLICATE_ID);
        }

        // 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(req.password());

        Admin admin = req.toEntity(encodePassword);


        // 관리자 저장
        adminRepository.save(admin);
    }

    @Transactional
    public void createMember(MemberCreateReq req) {

        passwordCheck(req.password(), req.passwordCheck());
        // 이메일 중복 체크
        boolean isDuplicated = memberRepository.existsByEmail(req.email());

        if (isDuplicated) {
            throw new ClientException(ExceptionType.CONFLICT_CREATE_DUPLICATE_EMAIL);
        }
        // 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(req.password());
        Member member = req.toEntity(encodePassword);

        // 멤버 저장
        memberRepository.save(member);
    }

    @Transactional
    public void logoutUser(String bearerToken, String refreshToken) {
        String accessToken = jwtTokenProperty.resolveToken(bearerToken);
        log.info("## 로그아웃 시도 - AT: {}, RT: {}", accessToken, refreshToken);
        // 엑세스 토큰 유효성 검사
        validRefreshToken(accessToken);
        // 리프레쉬 토큰 유효성 검사
        validRefreshToken(refreshToken);
        // Redis 토큰 가져오기
        RefreshToken saveToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ClientException(ExceptionType.UNAUTHORIZED_TOKEN_NOT_FOUND));
        // Redis에 저장된 토큰 삭제
        refreshTokenRepository.delete(saveToken);

        log.info("Logout successful userIdx: {}, Role: {}", saveToken.getUserIdx(), saveToken.getRoleType());
        // RefreshToken 제거후 accessToken은 블랙리스트 처리
        long expirationTime = jwtTokenProperty.getExpiration(accessToken);
        long now = System.currentTimeMillis();
        long ttl = (expirationTime - now) / 1000;

        if(ttl > 0) {
            redisTemplate.opsForValue().set(LOGOUT_ACCESS_TOKEN + accessToken, LOGOUT, ttl, TimeUnit.SECONDS);
        }

    }

    @Transactional
    public TokenDto reissue(String oldRefreshToken) {
        // 토큰 유효성 검사
        validRefreshToken(oldRefreshToken);
        // Redis에 토큰 존재 여부 확인후 삭제
        RefreshToken savedToken = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> {
                    log.error("## Redis에 토큰이 없음!!"); // 이 로그가 찍히는지 확인
                    return new ClientException(ExceptionType.UNAUTHORIZED_TOKEN_NOT_FOUND);
                });
        log.info("## Redis 조회 성공! 삭제 진행: {}", savedToken.getUserIdx());
        refreshTokenRepository.delete(savedToken);

        Long userIdx = savedToken.getUserIdx();
        RoleType roleType = savedToken.getRoleType();

        return tokenCreateAndSave(userIdx, roleType);
    }

    /**
     * 요청 받은 비밀번호 검증
     *
     * @param reqPassword     요청 받은 비밀번호
     * @param encodedPassword DB에 저장된 비밀번호
     * @param roleType        유저 타입
     */
    private void validatePasswordMatch(String reqPassword, String encodedPassword, RoleType roleType) {
        if (!passwordEncoder.matches(reqPassword, encodedPassword)) {
            if (roleType == RoleType.ADMIN) {
                throw new ClientException(ExceptionType.UNAUTHORIZED_LOGIN_FAIL_ADMIN);
            }
            throw new ClientException(ExceptionType.UNAUTHORIZED_LOGIN_FAIL_MEMBER);
        }
    }

    /**
     * Redis에 Refresh Token 저장 후 AccessToken, RefreshToken 객체 반환
     *
     * @param userIdx  사용자 idx
     * @param roleType 사용자 RoleType
     * @return 토큰 객체
     */
    private TokenDto tokenCreateAndSave(Long userIdx, RoleType roleType) {

        String accessToken = jwtTokenProperty.createToken(userIdx, roleType);
        String refreshToken = jwtTokenProperty.createRefreshToken(userIdx, roleType);

        refreshTokenRepository.save(RefreshToken.of(userIdx, roleType, refreshToken));
        return TokenDto.from(accessToken, refreshToken);
    }

    /**
     * 비밀번호가 같은지 체크
     *
     * @param password      비밀번호
     * @param passwordCheck 비밀번호 확인용
     */
    private void passwordCheck(String password, String passwordCheck) {
        if (!password.equals(passwordCheck)) {
            throw new ClientException(ExceptionType.BAD_REQUEST_PASSWORD_MISMATCH);
        }
    }

    /**
     * 리프레쉬 토큰 유효성 검사
     * @param refreshToken refreshToken
     */
    private void validRefreshToken(String refreshToken) {
        if (!jwtTokenProperty.validateToken(refreshToken)) {
            throw new ClientException(ExceptionType.UNAUTHORIZED_TOKEN_INVALID);
        }
    }

}
