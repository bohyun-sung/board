package com.toyproject.board.api.domain.post.repository;

import com.toyproject.board.api.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
