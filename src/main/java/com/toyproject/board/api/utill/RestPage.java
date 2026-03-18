package com.toyproject.board.api.utill;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class RestPage<T> extends PageImpl<T> {
    public RestPage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    @Override
    public long getTotalElements() {
        return super.getTotalElements();
    }
}
