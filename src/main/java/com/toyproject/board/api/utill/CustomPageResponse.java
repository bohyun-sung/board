package com.toyproject.board.api.utill;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomPageResponse<T> implements Page<T> {
    private final List<T> content;
    private final Pageable pageable;
    private final long totalElements;

    public CustomPageResponse(List<T> content, Pageable pageable, long total) {
        this.content = content;
        this.pageable = pageable;
        this.totalElements = total; // ✅ 여기서 보현님이 넘긴 '5'가 고정됩니다.
    }

    @Override
    public long getTotalElements() { return totalElements; }

    @Override
    public int getTotalPages() {
        return pageable.getPageSize() == 0 ? 1 : (int) Math.ceil((double) totalElements / pageable.getPageSize());
    }

    @Override
    public int getNumber() { return pageable.getPageNumber(); }

    @Override
    public int getSize() { return pageable.getPageSize(); }

    @Override
    public int getNumberOfElements() { return content.size(); }

    @Override
    public List<T> getContent() { return content; }

    @Override
    public boolean hasContent() { return !content.isEmpty(); }

    @Override
    public Sort getSort() { return pageable.getSort(); }

    @Override
    public boolean isFirst() { return !hasPrevious(); }

    @Override
    public boolean isLast() { return !hasNext(); }

    @Override
    public boolean hasNext() { return getNumber() + 1 < getTotalPages(); }

    @Override
    public boolean hasPrevious() { return getNumber() > 0; }

    @Override
    public Pageable nextPageable() { return hasNext() ? pageable.next() : Pageable.unpaged(); }

    @Override
    public Pageable previousPageable() { return hasPrevious() ? pageable.previousOrFirst() : Pageable.unpaged(); }

    @Override
    public Iterator<T> iterator() { return content.iterator(); }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        // 현재 리스트(T)를 새로운 리스트(U)로 변환합니다.
        List<U> convertedContent = this.content.stream()
                .map(converter)
                .collect(Collectors.toList());

        // 변환된 데이터를 담은 새로운 CustomPageResponse를 반환합니다.
        return new CustomPageResponse<>(convertedContent, this.pageable, this.totalElements);
    }
}