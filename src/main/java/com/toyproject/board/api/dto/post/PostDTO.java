package com.toyproject.board.api.dto.post;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.dto.upload.UploadsShowDto;
import com.toyproject.board.api.enums.BoardType;
import com.toyproject.board.api.enums.RoleType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostDTO {

    private final Long idx;
    private final String title;
    private final String content;
    private final Integer viewCount;
    private final BoardType boardType;
    private final RoleType roleType;
    private final Admin adminWriterIdx;
    private final LocalDateTime rgdt;
    private final LocalDateTime updt;
    private List<UploadsShowDto> uploads;


    private PostDTO(Long idx, String title, String content, Integer viewCount, BoardType boardType, RoleType roleType, Admin adminWriterIdx, LocalDateTime rgdt, LocalDateTime updt, List<UploadsShowDto> uploads) {
        this.idx = idx;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.boardType = boardType;
        this.roleType = roleType;
        this.adminWriterIdx = adminWriterIdx;
        this.rgdt = rgdt;
        this.updt = updt;
        this.uploads = uploads;
    }

    private PostDTO(Long idx, String title, String content, Integer viewCount, BoardType boardType, RoleType roleType, Admin adminWriterIdx, LocalDateTime rgdt, LocalDateTime updt) {
        this.idx = idx;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.boardType = boardType;
        this.roleType = roleType;
        this.adminWriterIdx = adminWriterIdx;
        this.rgdt = rgdt;
        this.updt = updt;
    }

    public static PostDTO from(Post entity) {
        return new PostDTO(
                entity.getIdx(),
                entity.getTitle(),
                entity.getContent(),
                entity.getViewCount(),
                entity.getBoardType(),
                entity.getRoleType(),
                entity.getAdmin(),
                entity.getRgdt(),
                entity.getUpdt()
        );
    }

    public static PostDTO fromAndUploadsAndViewCount(Post entity, List<UploadsShowDto> dtos, Integer viewCount) {
        return new PostDTO(
                entity.getIdx(),
                entity.getTitle(),
                entity.getContent(),
                viewCount,
                entity.getBoardType(),
                entity.getRoleType(),
                entity.getAdmin(),
                entity.getRgdt(),
                entity.getUpdt(),
                dtos
        );
    }
}
