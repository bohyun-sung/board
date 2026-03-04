package com.toyproject.board.api.dto.post.request;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.enums.BoardType;
import com.toyproject.board.api.enums.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "게시판 생성 요청 DTO")
public record PostCreateReq(
        @Schema(description = "제목", example = "테스트 제목 1")
        @NotBlank(message = "제목은 필수 입력 사항입니다.")
        @Size(max = 500, message = "제목은 500자 이내로 입력해주세요.")
        String title,

        @Schema(description = "본문", example = "테스트 본문 1")
        @NotBlank(message = "본문은 필수 입력 사항입니다.")
        String content,

        @Schema(description = "게시판 타입 (예: NEWS, NOTICE 등)", example = "NEWS")
        @NotNull(message = "게시판 타입은 필수 선택 사항입니다.")
        BoardType boardType,

        @Schema(description = "업로드 idx")
        List<Long> uploadIdxs


) {

    public Post toEntity(Admin admin, RoleType roleType) {
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .boardType(this.boardType)
                .roleType(roleType)
                .adminWriterIdx(admin)
                .build();
    }
}
