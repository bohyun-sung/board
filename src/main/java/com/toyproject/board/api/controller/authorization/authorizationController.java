package com.toyproject.board.api.controller.authorization;

import com.toyproject.board.api.config.Response;
import com.toyproject.board.api.dto.admin.request.AdminCreateReq;
import com.toyproject.board.api.dto.admin.request.AdminLoginReq;
import com.toyproject.board.api.dto.admin.response.AdminLoginRes;
import com.toyproject.board.api.service.authorization.AuthorizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[01] 로그인 회원가입", description = "회원가입 및 로그인")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class authorizationController {

    private final AuthorizationService authorizationService;

    @PostMapping("/login/admin")
    @Operation(summary = "관리자 계정 로그인", description = "관리자 로그인")
    public Response<AdminLoginRes> loginAdmin(@Validated @RequestBody AdminLoginReq req) {
        return Response.success(AdminLoginRes.from(authorizationService.loginAdmin(req)));
    }

    @PostMapping("/create/admin")
    @Operation(summary = "관리자 계정 생성", description = "신규 계정 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 생성 성공"),
            @ApiResponse(responseCode = "400", description = "중복 아이디")
    })
    public Response<Void> createAdmin(@Valid @RequestBody AdminCreateReq req) {
        authorizationService.createAdmin(req);
        return Response.success();
    }
}
