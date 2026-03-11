package com.toyproject.board.api.domain.comment.repository;

import com.toyproject.board.api.domain.comment.entity.Comment;
import com.toyproject.board.api.domain.comment.repository.querydsl.CommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
}
