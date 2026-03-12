package com.toyproject.board.api.dto.post.response;

import com.toyproject.board.api.dto.post.PostDTO;
import com.toyproject.board.api.dto.upload.UploadsShowDto;
import com.toyproject.board.api.enums.BoardType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "게시물 상세보기 RES")
public record PostShowRes(
        @Schema(description = "게시물 식별자", example = "1")
        Long postIdx,

        @Schema(description = "제목", example = "테스트 제목 1")
        String title,

        @Schema(description = "본문", example = "1")
        String content,

        @Schema(description = "게시물 종류", example = "NEWS")
        BoardType boardType,

        @Schema(description = "조회수", example = "1")
        Integer viewCount,

        @Schema(description = "게시물 생성시간", example = "2026-02-06T01:37:33")
        LocalDateTime rgdt,

        @Schema(description = "게시물 업로드 파일")
        List<UploadsShowDto> uploads,

        @Schema(description = "작성자")
        String nickname,

        @Schema(description = "관리자 idx")
        Long adminIdx,

        @Schema(description = "멤버 idx")
        Long memberIdx


        ) {
    public static PostShowRes from(PostDTO dto) {
        return new PostShowRes(
                dto.getIdx(),
                dto.getTitle(),
                dto.getContent(),
                dto.getBoardType(),
                dto.getViewCount(),
                dto.getRgdt(),
                dto.getUploads(),
                dto.getNickname(),
                dto.getAdminIdx(),
                dto.getMemberIdx()
        );
    }
}
