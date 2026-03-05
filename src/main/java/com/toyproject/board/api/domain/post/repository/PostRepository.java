package com.toyproject.board.api.domain.post.repository;

import com.toyproject.board.api.domain.post.entity.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Modifying
    @Query("update Post p set p.viewCount = p.viewCount + :viewCount where p.idx = :postIdx")
    void updateViewCount(@Param("postIdx") Long postIdx, @Param("viewCount") Long viewCount);
}
