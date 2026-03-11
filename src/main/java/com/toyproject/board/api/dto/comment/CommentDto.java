package com.toyproject.board.api.dto.comment;

import com.toyproject.board.api.domain.comment.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long commentIdx;
    private String content;
    private String nickname;
    private LocalDateTime rgdt;
    private boolean isDeleted;
    @Builder.Default
    private List<CommentDto> children = new ArrayList<>();

    // 단일 엔티티를 DTO로 변환 (자식 제외)
    public static CommentDto from(Comment entity) {
        return CommentDto.builder()
                .commentIdx(entity.getIdx())
                .content(entity.isDeleted() ? "삭제된 댓글입니다." : entity.getContent())
                .nickname(entity.getMember().getNickname())
                .rgdt(entity.getRgdt())
                .isDeleted(entity.isDeleted())
                .children(new ArrayList<>())
                .build();
    }
}
