package com.toyproject.board.api.domain.post.repository.querydsl;

import com.toyproject.board.api.dto.post.PostListDto;
import com.toyproject.board.api.enums.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostListDto> findAllByCondition(String title, String content, String nickname, BoardType boardType, Pageable pageable);
}
