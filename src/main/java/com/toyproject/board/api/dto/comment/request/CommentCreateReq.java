package com.toyproject.board.api.dto.comment.request;

import com.toyproject.board.api.domain.comment.entity.Comment;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentCreateReq {
    @NotBlank(message = "{NotBlank.comment.content}")
    @Schema(description = "댓글 내용", example = "테스트 댓글_1")
    private String content;

    @Schema(description = "부모 댓글 PK (Root comment null)", example = "1")
    private Long parentIdx;

    @Schema(description = "첨부 파일 PK 리스트", example = "[1, 2]")
    private List<Long> uploadIdxs = new ArrayList<>();

    public Comment toEntity(Post post, Member member, Comment parentComment) {
        return Comment.of(this.content, post, member, parentComment);
    }
}
