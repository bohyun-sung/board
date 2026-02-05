package com.toyproject.board.api.domain.post.entity;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.base.DefaultTimeStampEntity;
import com.toyproject.board.api.enums.BoardType;
import com.toyproject.board.api.enums.coverter.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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
    private Admin adminWriterIdx;


    @Builder
    @SuppressWarnings("unsed")
    public Post(String title, String content, BoardType boardType, RoleType roleType, Admin adminWriterIdx) {
        this.title = title;
        this.content = content;
        this.boardType = boardType;
        this.roleType = roleType;
        this.adminWriterIdx = adminWriterIdx;
    }

    public void increaseViewCount() {
        this.viewCount ++;
    }
}
