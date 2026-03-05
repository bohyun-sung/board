package com.toyproject.board.api.controller.auth;

import com.toyproject.board.api.config.Response;
import com.toyproject.board.api.constants.AuthConstants;
import com.toyproject.board.api.dto.admin.AdminLoginDto;
import com.toyproject.board.api.dto.auth.TokenDto;
import com.toyproject.board.api.dto.auth.request.AdminCreateReq;
import com.toyproject.board.api.dto.auth.request.AdminLoginReq;
import com.toyproject.board.api.dto.auth.request.MemberLoginReq;
import com.toyproject.board.api.dto.auth.response.AdminLoginRes;
import com.toyproject.board.api.dto.auth.response.MemberLoginRes;
import com.toyproject.board.api.dto.member.MemberLoginDto;
import com.toyproject.board.api.dto.member.request.MemberCreateReq;
import com.toyproject.board.api.jwt.properties.AppJwtProperties;
import com.toyproject.board.api.service.authorization.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[01] 로그인 회원가입", description = "회원가입 및 로그인")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AppJwtProperties appJwtProperties;

    @Operation(summary = "관리자 계정 로그인", description = "관리자 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 인증 정보"),
    })
    @PostMapping("/login/admin")
    public Response<AdminLoginRes> loginAdmin(@RequestBody @Validated AdminLoginReq req, HttpServletResponse response) {

        AdminLoginDto adminLoginDto = authService.loginAdmin(req);

        ResponseCookie cookie = ResponseCookie.from(AuthConstants.REFRESH_TOKEN, adminLoginDto.getTokenDto().refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(appJwtProperties.getRefreshTTL()) // 14일
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.success(AdminLoginRes.from(adminLoginDto));
    }

    @Operation(summary = "멤버 계정 로그인", description = "이메일, 비밀번호 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 인증 정보"),
    })
    @PostMapping("/login/member")
    public Response<MemberLoginRes> loginMember(@RequestBody @Validated MemberLoginReq req, HttpServletResponse response) {
        MemberLoginDto memberLoginDto = authService.loginMember(req);

        ResponseCookie cookie = ResponseCookie.from(AuthConstants.REFRESH_TOKEN, memberLoginDto.getTokenDto().refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(appJwtProperties.getRefreshTTL()) // 14일
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.success(MemberLoginRes.from(memberLoginDto));
    }

    @Operation(summary = "관리자 계정 생성", description = "신규 계정 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 생성 성공"),
            @ApiResponse(responseCode = "409", description = "중복 아이디")
    })
    @PostMapping("/create/admin")
    public Response<Void> createAdmin(@RequestBody @Validated AdminCreateReq req) {
        authService.createAdmin(req);
        return Response.success();
    }

    @Operation(summary = "멤버 계정 생성", description = "신규 계정 생성 email, password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 생성 성공"),
            @ApiResponse(responseCode = "409", description = "이미 사용중인 이메일")
    })
    @PostMapping("/create/member")
    public Response<Void> createMember(@RequestBody @Validated MemberCreateReq req) {
        authService.createMember(req);
        return Response.success();
    }

    @Operation(summary = "리프레쉬 토큰 재발급", description = "신규 엑세스 토큰 && 신규 리프레쉬 토큰 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 인증 정보"),
    })
    @PostMapping("/reissue")
    public Response<TokenDto> reissue(@CookieValue(name = AuthConstants.REFRESH_TOKEN) String refreshToken, HttpServletResponse response) {
        TokenDto tokenDto = authService.reissue(refreshToken);

        ResponseCookie cookie = ResponseCookie.from(AuthConstants.REFRESH_TOKEN, tokenDto.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(appJwtProperties.getRefreshTTL())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return Response.success(tokenDto);
    }
}
