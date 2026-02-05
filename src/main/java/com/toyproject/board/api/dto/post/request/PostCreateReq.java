package com.toyproject.board.api.dto.post.request;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.enums.BoardType;
import com.toyproject.board.api.enums.coverter.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시판 생성 DTO")
public record PostCreateReq(
        @Schema(description = "제목", example = "테스트 제목 1")
        String title,
        @Schema(description = "본문", example = "테스트 본문 1")
        String content,
        @Schema(description = "제목", example = "NEWS")
        BoardType boardType
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
