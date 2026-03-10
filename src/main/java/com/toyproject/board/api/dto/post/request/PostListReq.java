package com.toyproject.board.api.dto.post.request;

import com.toyproject.board.api.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시판 REQ")
public record PostListReq(
        @Schema(description = "게시판 제목", example = "테스트")
        String title,
        @Schema(description = "게시판 내용", example = "테스트")
        String content,
        @Schema(description = "작성자 관리자는 관리자로 표시", example = "홍길동")
        String nickname,
        @Schema(description = "게시판 타입 (예: NEWS, NOTICE 등)", example = "FREE")
        BoardType boardType
) {
}
