package com.toyproject.board.api.dto.post.request;

import com.toyproject.board.api.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


public record PostUpdateReq(
        @Schema(description = "제목", example = "테스트 제목 1")
        String title,
        @Schema(description = "본문", example = "테스트 본문 1")
        String content,
        @Schema(description = "게시판 타입", example = "NEWS")
        BoardType boardType,
        @Schema(description = "업로드 idx")
        List<Long> uploadIdxs
) {
}
