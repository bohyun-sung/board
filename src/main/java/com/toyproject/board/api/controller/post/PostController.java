package com.toyproject.board.api.controller.post;

import com.toyproject.board.api.annotations.swagger.ApiCommonResponses;
import com.toyproject.board.api.annotations.CurrentUserIdx;
import com.toyproject.board.api.annotations.CurrentUserRoleType;
import com.toyproject.board.api.annotations.swagger.BadRequestErrorResponse;
import com.toyproject.board.api.annotations.swagger.UnauthorizedErrorResponse;
import com.toyproject.board.api.config.Response;
import com.toyproject.board.api.dto.post.request.PostCreateReq;
import com.toyproject.board.api.dto.post.request.PostListReq;
import com.toyproject.board.api.dto.post.request.PostUpdateReq;
import com.toyproject.board.api.dto.post.response.PostListRes;
import com.toyproject.board.api.dto.post.response.PostShowRes;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[02] 게시물")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공")
    @Operation(summary = "게시물 목록", description = "게시물 목록 제목, 닉네임(관리자는 관리자로 표기), 조회수 ... 최신 등록순 정렬")
    @GetMapping
    public Response<Page<PostListRes>> getPostList(
            @ParameterObject PostListReq req,
            @ParameterObject @PageableDefault(sort = "rgdt", direction = Sort.Direction.DESC) Pageable pageable) {

        return Response.success(postService.getPostList(req, pageable).map(PostListRes::from));
    }

    @Operation(summary = "게시물 상세보기", description = "게시물 상세 내용 조회, 성공 시 조회수 1 증가")
    @Parameters({
            @Parameter(name = "postIdx", description = "게시물 식별 고유 번호", example = "1")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 게시물을 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @GetMapping("/{postIdx}")
    public Response<PostShowRes> showPost(
            @PathVariable Long postIdx,
            HttpServletRequest request) {
        return Response.success(PostShowRes.from(postService.showPost(postIdx, request)));
    }

    @ApiResponse(responseCode = "403", description = "업로드된 파일 권한 없음")
    @ApiCommonResponses
    @Operation(summary = "게시물 작성", description = "게시물 작성")
    @PostMapping
    public Response<Void> createPost(
            @RequestBody @Validated PostCreateReq req,
            @CurrentUserIdx @Parameter(hidden = true) Long userIdx,
            @CurrentUserRoleType @Parameter(hidden = true) RoleType roleType) {
        postService.createPost(req, userIdx, roleType);
        return Response.success();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "403", description = "업로드된 파일 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시물 정보를 찾지 못했음"),
    })
    @ApiCommonResponses
    @Operation(summary = "게시물 수정", description = "게시물 수정")
    @PatchMapping("/{postIdx}")
    public Response<Void> updatePost(
            @PathVariable Long postIdx,
            @CurrentUserIdx @Parameter(hidden = true) Long userIdx,
            @CurrentUserRoleType @Parameter(hidden = true) RoleType roleType,
            @RequestBody @Validated PostUpdateReq req) {
        postService.updatePost(postIdx, userIdx, roleType, req);
        return Response.success();
    }

    @ApiCommonResponses
    @Operation(summary = "게시물 삭제", description = "게시물 삭제")
    @DeleteMapping("/{postIdx}")
    public Response<Void> deletePost(
            @PathVariable Long postIdx,
            @CurrentUserIdx @Parameter(hidden = true) Long userIdx,
            @CurrentUserRoleType @Parameter(hidden = true) RoleType roleType) {
        postService.deletePost(postIdx, userIdx, roleType);
        return Response.success();
    }


}
