package com.toyproject.board.api.domain.post.entity;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.base.DefaultTimeStampEntity;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.enums.BoardType;
import com.toyproject.board.api.enums.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
@Entity
public class Post extends DefaultTimeStampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_idx", columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Lob
    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "board_type")
    @Convert(converter = BoardType.Converter.class)
    private BoardType boardType;

    @Column(name = "role_type", nullable = false)
    @Convert(converter = RoleType.Converter.class)
    private RoleType roleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_idx", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    private Post(String title, String content, BoardType boardType, RoleType roleType, Admin admin, Member member) {
        this.title = title;
        this.content = content;
        this.boardType = boardType;
        this.roleType = roleType;
        this.admin = admin;
        this.member = member;
    }

    public static Post of(String title, String content, BoardType boardType, RoleType roleType, Admin admin, Member member) {
        return new Post(title, content, boardType, roleType, admin, member);
    }
    public void update(String title, String content, BoardType boardType) {
        this.title = title;
        this.content = content;
        this.boardType = boardType;
    }
}
