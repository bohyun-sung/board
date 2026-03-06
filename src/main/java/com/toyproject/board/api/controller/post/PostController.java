package com.toyproject.board.api.controller.post;

import com.toyproject.board.api.annotations.CurrentUserIdx;
import com.toyproject.board.api.annotations.CurrentUserRoleType;
import com.toyproject.board.api.config.Response;
import com.toyproject.board.api.dto.post.request.PostCreateReq;
import com.toyproject.board.api.dto.post.request.PostUpdateReq;
import com.toyproject.board.api.dto.post.response.PostShowRes;
import com.toyproject.board.api.dto.upload.response.UploadsRes;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.enums.UploadType;
import com.toyproject.board.api.service.post.PostService;
import com.toyproject.board.api.service.upload.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "[02] 게시판")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;
    private final S3Service s3Service;

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

    @Operation(summary = "게시물 작성", description = "게시물 작성")
    @PostMapping
    public Response<Void> createPost(
            @RequestBody @Validated PostCreateReq req,
            @CurrentUserIdx @Parameter(hidden = true) Long userIdx,
            @CurrentUserRoleType @Parameter(hidden = true) RoleType roleType) {
        postService.createPost(req, userIdx, roleType);
        return Response.success();
    }

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

    @Operation(summary = "게시물 삭제", description = "게시물 삭제")
    @DeleteMapping
    public Response<Void> deletePost(@PathVariable Long postIdx) {
        postService.deletePost(postIdx);
        return Response.success();
    }

    @Operation(summary = "게시물 이미지 업로드", description = "bulk upload")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<List<UploadsRes>> uploadFiles(
            @RequestPart("files") List<MultipartFile> files,
            @CurrentUserIdx @Parameter(hidden = true) Long userIdx,
            @CurrentUserRoleType @Parameter(hidden = true) RoleType roleType) {
        return Response.success(s3Service.uploadMultipleFiles(files, userIdx, roleType, UploadType.POST));
    }
}
