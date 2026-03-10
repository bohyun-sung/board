package com.toyproject.board.api.dto.post;

import com.querydsl.core.annotations.QueryProjection;
import com.toyproject.board.api.enums.BoardType;
import com.toyproject.board.api.enums.RoleType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostListDto {
    private final Long postIdx;
    private final String title;
    private final BoardType boardType;
    private Integer viewCount;
    private final RoleType roleType;
    private final LocalDateTime rgdt;
    private final String nickname;

    @QueryProjection
    public PostListDto(Long postIdx, String title, BoardType boardType, Integer viewCount, RoleType roleType, LocalDateTime rgdt, String nickname) {
        this.postIdx = postIdx;
        this.title = title;
        this.boardType = boardType;
        this.viewCount = viewCount;
        this.roleType = roleType;
        this.rgdt = rgdt;
        this.nickname = nickname;
    }

    public void updateViewCount(int totalCount) {
        this.viewCount = totalCount;
    }
}
