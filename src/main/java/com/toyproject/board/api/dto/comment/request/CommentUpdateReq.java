package com.toyproject.board.api.dto.comment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Schema(description = "댓글 생성 REQ")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentUpdateReq {

    @NotBlank(message = "{NotBlank.comment.content}")
    @Schema(description = "댓글 내용", example = "테스트 댓글_수정_1")
    private String content;

    @Schema(description = "첨부 파일 PK 리스트", example = "[1, 2]")
    private List<Long> uploadIdxs = new ArrayList<>();
}
