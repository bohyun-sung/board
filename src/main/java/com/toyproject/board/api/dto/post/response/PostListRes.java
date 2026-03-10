package com.toyproject.board.api.dto.post.response;

import com.toyproject.board.api.dto.post.PostListDto;
import com.toyproject.board.api.enums.BoardType;
import com.toyproject.board.api.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "게시판 목록 RES")
public record PostListRes(
        @Schema(description = "게시판 idx")
        Long postIdx,
        @Schema(description = "게시판 제목")
        String title,
        @Schema(description = "작성자 관리자는 관리자로 표시")
        String nickname,
        @Schema(description = "게시판 타입")
        BoardType boardType,
        @Schema(description = "조회수")
        Integer viewCount,
        @Schema(description = "등록시간")
        LocalDateTime rgdt
) {
        public PostListRes(Long postIdx, String title, String nickname, BoardType boardType, Integer viewCount, LocalDateTime rgdt) {
                this.postIdx = postIdx;
                this.title = title;
                this.nickname = nickname;
                this.boardType = boardType;
                this.viewCount = viewCount;
                this.rgdt = rgdt;
        }

        public static PostListRes from(PostListDto dto) {
                return new PostListRes(
                        dto.getPostIdx(),
                        dto.getTitle(),
                        (dto.getRoleType() == RoleType.ADMIN) ? "관리자" : dto.getNickname(),
                        dto.getBoardType(),
                        dto.getViewCount(),
                        dto.getRgdt()
                );
        }
}
