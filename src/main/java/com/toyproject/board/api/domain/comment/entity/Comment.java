package com.toyproject.board.api.domain.comment.entity;

import com.toyproject.board.api.domain.base.DefaultTimeStampEntity;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
@Entity
public class Comment extends DefaultTimeStampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_idx", columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_idx")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_idx")
    private Comment parentComment;

    @Column(name = "root_comment_idx")
    private Long rootCommentIdx;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public Comment(String content, Post post, Member member, Comment parentComment, Long rootCommentIdx, String path) {
        this.content = content;
        this.post = post;
        this.member = member;
        this.parentComment = parentComment;
        this.rootCommentIdx = rootCommentIdx;
        this.path = path;
    }

    public static Comment of(String content, Post post, Member member, Comment parentComment, Long rootCommentIdx, String parentPath) {
        return new Comment(content, post, member, parentComment, rootCommentIdx, parentPath);
    }

    public void modifyContent(String content) {
        this.content = content;
    }

    public void modifyIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void modifyPath(String currentPath) {
        this.path = currentPath;
    }
}
