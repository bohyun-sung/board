package com.toyproject.board.api.controller.post;

import com.toyproject.board.api.config.Response;
import com.toyproject.board.api.dto.post.request.PostCreateReq;
import com.toyproject.board.api.dto.post.response.PostShowRes;
import com.toyproject.board.api.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[02] 게시판")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

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
    public Response<PostShowRes> showPost(@PathVariable Long postIdx) {
        return Response.success(PostShowRes.from(postService.showPost(postIdx)));
    }

    @Operation(summary = "게시물 작성", description = "게시물 작성")
    @PostMapping
    public Response<Void> createPost(PostCreateReq req) {
        postService.createPost(req);
        return Response.success();
    }
}
