package com.toyproject.board.api.controller.post;

import com.toyproject.board.api.config.Response;
import com.toyproject.board.api.dto.post.request.PostCreateReq;
import com.toyproject.board.api.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[02] 게시판")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "게시물 작성", description = "게시물 작성")
    public Response<Void> createPost(PostCreateReq req) throws BadRequestException {
        postService.createPost(req);
        return Response.success();
    }
}
