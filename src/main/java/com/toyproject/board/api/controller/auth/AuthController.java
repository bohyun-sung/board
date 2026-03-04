package com.toyproject.board.api.controller.auth;

import com.toyproject.board.api.config.Response;
import com.toyproject.board.api.dto.admin.AdminLoginDto;
import com.toyproject.board.api.dto.auth.request.AdminCreateReq;
import com.toyproject.board.api.dto.auth.request.AdminLoginReq;
import com.toyproject.board.api.dto.auth.response.AdminLoginRes;
import com.toyproject.board.api.dto.auth.response.TokenRes;
import com.toyproject.board.api.service.authorization.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[01] 로그인 회원가입", description = "회원가입 및 로그인")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "관리자 계정 로그인", description = "관리자 로그인")
    @PostMapping("/login/admin")
    public Response<AdminLoginRes> loginAdmin(@Validated @RequestBody AdminLoginReq req, HttpServletResponse response) {

        AdminLoginDto adminLoginDto = authService.loginAdmin(req);
        // Refresh Token을 쿠키에 설정
        ResponseCookie cookie = ResponseCookie.from("refreshToken", adminLoginDto.getRefreshToken())
                .httpOnly(true)
                .secure(true) // HTTPS 환경에서 필수
                .path("/")
                .maxAge(1209600) // 14일
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return Response.success(AdminLoginRes.from(adminLoginDto));
    }

    @Operation(summary = "관리자 계정 생성", description = "신규 계정 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 생성 성공"),
            @ApiResponse(responseCode = "400", description = "중복 아이디")
    })
    @PostMapping("/create/admin")
    public Response<Void> createAdmin(@Valid @RequestBody AdminCreateReq req) {
        authService.createAdmin(req);
        return Response.success();
    }

    @Operation(summary = "리프레쉬 토큰 재발급")
    @PostMapping("/reissue")
    public Response<TokenRes> reissue(@CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response) {
        TokenRes tokenRes = authService.reissue(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenRes.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(1209600)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return Response.success(tokenRes);
    }
}
