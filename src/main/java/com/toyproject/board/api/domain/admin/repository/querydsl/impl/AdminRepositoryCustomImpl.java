package com.toyproject.board.api.domain.admin.repository.querydsl.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.entity.QAdmin;
import com.toyproject.board.api.domain.admin.repository.querydsl.AdminRepositoryCustom;
import com.toyproject.board.api.utill.QueryDslUtil;
import com.toyproject.board.api.utill.QueryDslWhereBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.qos.logback.core.util.StringUtil.notNullNorEmpty;
import static com.toyproject.board.api.domain.admin.entity.QAdmin.admin;

@RequiredArgsConstructor
public class AdminRepositoryCustomImpl implements AdminRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Admin> findAllByCondition(String name, String phone, String email, Pageable pageable) {

        BooleanBuilder where = QueryDslWhereBuilder.init()
                .andContains(admin.name, name)
                .andContains(admin.email, email)
                .andContains(admin.phone, phone)
                .build();

        Map<String, Expression<?>> sortMap = new HashMap<>();
        sortMap.put("rgdt", admin.rgdt);
        sortMap.put("adminIdx", admin.idx);

        OrderSpecifier<?>[] order = QueryDslUtil.getSafeOrderSpecifier(pageable.getSort(), sortMap, admin.rgdt.desc());

        List<Admin> content = queryFactory
                .select(admin)
                .from(admin)
                .where(where)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(order)
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(admin.count())
                .from(admin)
                .where(where);

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }
}
