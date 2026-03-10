package com.toyproject.board.api.controller.comment;

import com.toyproject.board.api.annotations.CurrentUserIdx;
import com.toyproject.board.api.annotations.CurrentUserRoleType;
import com.toyproject.board.api.annotations.swagger.ApiCommonResponses;
import com.toyproject.board.api.config.Response;
import com.toyproject.board.api.dto.comment.request.CommentCreateReq;
import com.toyproject.board.api.dto.comment.request.CommentUpdateReq;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponse(responseCode = "404", description = "게시물, 멤버, 댓글 을 찾을 수 없습니다")
    @Operation(summary = "댓글 생성", description = "댓글 생성")
    @PostMapping("/posts/{postIdx}/comments")
    public Response<Void> createComment(
            @PathVariable Long postIdx,
            @CurrentUserIdx @Parameter(hidden = true) Long userIdx,
            @CurrentUserRoleType @Parameter(hidden = true) RoleType roleType,
            @RequestBody @Validated CommentCreateReq req) {

        commentService.createComment(postIdx, userIdx, roleType, req);
        return Response.success();
    }

    @ApiCommonResponses
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다"),
            @ApiResponse(responseCode = "410", description = "이미 삭제된 댓글은 접근할 수 없습니다"),
    })
    @Operation(summary = "댓글 수정", description = "댓글 수정")
    @PatchMapping("/comments/{commentIdx}")
    public Response<Void> updateComment(
            @PathVariable Long commentIdx,
            @CurrentUserIdx @Parameter(hidden = true) Long userIdx,
            @CurrentUserRoleType @Parameter(hidden = true) RoleType roleType,
            @RequestBody CommentUpdateReq req) {
        commentService.updateComment(commentIdx, userIdx, roleType, req);
        return Response.success();

    }

    @ApiCommonResponses
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다"),
            @ApiResponse(responseCode = "410", description = "이미 삭제된 댓글입니다")
    })
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제 처리합니다. (대댓글이 있는 경우 구조 유지를 위해 상태만 변경)")
    @DeleteMapping("/comments/{commentIdx}")
    public Response<Void> deleteComment(
            @PathVariable Long commentIdx,
            @CurrentUserIdx @Parameter(hidden = true) Long userIdx,
            @CurrentUserRoleType @Parameter(hidden = true) RoleType roleType) {
        commentService.deleteComment(commentIdx, userIdx, roleType);
        return Response.success();
    }

}
