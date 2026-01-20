package com.toyproject.board.api.controller.admin;

import com.toyproject.board.api.config.Response;
import com.toyproject.board.api.dto.admin.request.AdminCreateReq;
import com.toyproject.board.api.dto.admin.request.AdminSearchAdminReq;
import com.toyproject.board.api.dto.admin.response.AdminSearchAdminRes;
import com.toyproject.board.api.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[00] 관리자 ", description = "관리자 계정 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    @Operation(summary = "관리자 검색", description = "핸드폰, 이름, 이메일 검색")
    public Response<Page<AdminSearchAdminRes>> searchAdmin(
            @ModelAttribute AdminSearchAdminReq req,
            @ParameterObject
            @PageableDefault(size = 10, sort = "rgdt", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        return Response.success(
                adminService.searchAdmin(
                        req.getPhone(),
                        req.getName(),
                        req.getEmail(),
                        pageable
                ).map(AdminSearchAdminRes::from)
        );
    }

    @PostMapping
    @Operation(summary = "관리자 계정 생성", description = "신규 계정 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 생성 성공"),
            @ApiResponse(responseCode = "400", description = "중복 아이디")
    })
    public Response<Void> createAdmin(@Valid @RequestBody AdminCreateReq req) {
        adminService.createAdmin(req);
        return Response.success();
    }
}
