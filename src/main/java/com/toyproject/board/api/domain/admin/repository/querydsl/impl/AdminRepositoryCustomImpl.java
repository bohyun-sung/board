package com.toyproject.board.api.domain.admin.repository.querydsl.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.entity.QAdmin;
import com.toyproject.board.api.domain.admin.repository.querydsl.AdminRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static ch.qos.logback.core.util.StringUtil.notNullNorEmpty;

@RequiredArgsConstructor
public class AdminRepositoryCustomImpl implements AdminRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Admin> findAllByCondition(String name, String phone, String email, Pageable pageable) {

        List<Admin> content = jpaQueryFactory
                .select(QAdmin.admin)
                .from(QAdmin.admin)
                .where(
                        containsName(name),
                        containsEmail(email),
                        containsPhone(phone)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable.getSort()))
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(QAdmin.admin.count())
                .from(QAdmin.admin)
                .where(
                        containsName(name),
                        containsEmail(email),
                        containsPhone(phone)
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression containsName(String name) {
        return notNullNorEmpty(name) ? QAdmin.admin.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression containsEmail(String email) {
        return notNullNorEmpty(email) ? QAdmin.admin.email.containsIgnoreCase(email) : null;
    }

    private BooleanExpression containsPhone(String phone) {
        return notNullNorEmpty(phone) ? QAdmin.admin.phone.containsIgnoreCase(phone) : null;
    }

    // 정렬 적용 예시
    private OrderSpecifier<?>[] getOrderSpecifier(Sort sort) {
        return sort.stream()
                .map(order -> {
                    PathBuilder<Admin> pathBuilder = new PathBuilder<>(QAdmin.admin.getType(), QAdmin.admin.getMetadata());
                    return new OrderSpecifier(
                            order.isAscending() ? Order.ASC : Order.DESC,
                            pathBuilder.get(order.getProperty())
                    );
                })
                .toArray(OrderSpecifier[]::new);
    }

}
