package com.toyproject.board.api.domain.comment.entity;

import com.toyproject.board.api.domain.base.DefaultTimeStampEntity;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.post.entity.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    @OrderBy("rgdt ASC")
    private List<Comment> childrenComment = new ArrayList<>();

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public Comment(String content, Post post, Member member, Comment parentComment) {
        this.content = content;
        this.post = post;
        this.member = member;
        this.parentComment = parentComment;
    }

    public static Comment of(String content, Post post, Member member, Comment parentComment) {
        return new Comment(content, post, member, parentComment);
    }

    public void modifyContent(String content) {
        this.content = content;
    }

    public void modifyIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
