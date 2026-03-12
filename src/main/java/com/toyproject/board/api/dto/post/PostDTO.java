package com.toyproject.board.api.dto.post;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.dto.upload.UploadsShowDto;
import com.toyproject.board.api.enums.BoardType;
import com.toyproject.board.api.enums.RoleType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PostDTO {

    private final Long idx;
    private final String title;
    private final String content;
    private final Integer viewCount;
    private final BoardType boardType;
    private final RoleType roleType;
    private final Long adminIdx;
    private final Long memberIdx;
    private final LocalDateTime rgdt;
    private final LocalDateTime updt;
    private List<UploadsShowDto> uploads = new ArrayList<>();
    private final String nickname;




    private PostDTO(Long idx, String title, String content, Integer viewCount, BoardType boardType, RoleType roleType, Admin admin, Member member, LocalDateTime rgdt, LocalDateTime updt, List<UploadsShowDto> uploads, String nickname) {
        this.idx = idx;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.boardType = boardType;
        this.roleType = roleType;
        this.adminIdx = (admin != null) ? admin.getIdx() : null;
        this.memberIdx = (member != null) ? member.getIdx() : null;
        this.rgdt = rgdt;
        this.updt = updt;
        this.uploads = uploads;
        this.nickname = nickname;
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
                entity.getMember(),
                entity.getRgdt(),
                entity.getUpdt(),
                dtos,
                (entity.getRoleType() == RoleType.ADMIN) ? "관리자" : entity.getMember().getNickname()
        );
    }
}
