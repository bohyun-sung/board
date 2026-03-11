package com.toyproject.board.api.domain.comment.repository.querydsl;

import com.toyproject.board.api.dto.comment.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    Page<CommentDto> findByCondition(Long postIdx, Pageable pageable);
}
