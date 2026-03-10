package com.toyproject.board.api.utill;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.util.StringUtils;

import java.util.Collection;

public class QueryDslWhereBuilder {


    // 내부적으로만 사용하는 빌더
    private final BooleanBuilder builder;

    private QueryDslWhereBuilder() {
        this.builder = new BooleanBuilder();
    }

    public static QueryDslWhereBuilder init() {
        return new QueryDslWhereBuilder();
    }

    // 최종적으로 Querydsl where()에 넣기 위한 반환 메서드
    public BooleanBuilder build() {
        return this.builder;
    }

    // --- 체이닝 메서드들 ---

    public <T> QueryDslWhereBuilder andEq(SimpleExpression<T> path, T value) {
        if (value != null) {
            builder.and(path.eq(value));
        }
        return this;
    }

    public QueryDslWhereBuilder andContains(StringPath path, String value) {
        if (StringUtils.hasText(value)) {
            builder.and(path.containsIgnoreCase(value));
        }
        return this;
    }

    public <T> QueryDslWhereBuilder andIn(SimpleExpression<T> path, Collection<T> values) {
        if (values != null && !values.isEmpty()) {
            builder.and(path.in(values));
        }
        return this;
    }

}
