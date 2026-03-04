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
    private String title;
    private String content;
    private Integer viewCount;
    private BoardType boardType;
    private RoleType roleType;
    private Admin adminWriterIdx;
    private LocalDateTime rgdt;
    private LocalDateTime updt;
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
                entity.getAdminWriterIdx(),
                entity.getRgdt(),
                entity.getUpdt()
        );
    }

    public static PostDTO fromAndUploads(Post entity, List<UploadsShowDto> dtos) {
        return new PostDTO(
                entity.getIdx(),
                entity.getTitle(),
                entity.getContent(),
                entity.getViewCount(),
                entity.getBoardType(),
                entity.getRoleType(),
                entity.getAdminWriterIdx(),
                entity.getRgdt(),
                entity.getUpdt(),
                dtos
        );
    }
}
