package com.toyproject.board.api.controller.comment;

import com.toyproject.board.api.annotations.CurrentUserIdx;
import com.toyproject.board.api.annotations.CurrentUserRoleType;
import com.toyproject.board.api.annotations.swagger.ApiCommonResponses;
import com.toyproject.board.api.config.Response;
import com.toyproject.board.api.dto.comment.request.CommentCreateReq;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[04] 댓글")
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CommentController {

    private final CommentService commentService;

    @ApiCommonResponses
    @PostMapping("/posts/{postIdx}/comments")
    public Response<Void> createComment(
            @PathVariable Long postIdx,
            @CurrentUserIdx @Parameter(hidden = true) Long userIdx,
            @CurrentUserRoleType @Parameter(hidden = true) RoleType roleType,
            @RequestBody @Validated CommentCreateReq req) {

        commentService.createComment(postIdx, userIdx, roleType, req);
        return Response.success();
    }

}
