package com.toyproject.board.api.dto.post;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.enums.BoardType;
import com.toyproject.board.api.enums.coverter.RoleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostDTO {

    private final Long idx;
    private String title;
    private String content;
    private Integer viewCount;
    private BoardType boardType;
    private RoleType roleType;
    private Admin adminWriterIdx;
    private LocalDateTime rgdt;
    private LocalDateTime updt;

    public static PostDTO from(Post entity) {
        return new PostDTO(
                entity.getIdx(),
                entity.getTitle(),
                entity.getContent(),
                entity.getViewCount(),
                entity.getBoardType(),
                entity.getRoleType(),
                entity.getAdminWriterIdx(),
                entity.getRgdt(),
                entity.getUpdt()
        );
    }
}
