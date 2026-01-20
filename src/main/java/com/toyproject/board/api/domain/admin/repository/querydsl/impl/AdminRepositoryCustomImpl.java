package com.toyproject.board.api.domain.admin.repository.querydsl.impl;

import ch.qos.logback.core.util.StringUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.entity.QAdmin;
import com.toyproject.board.api.domain.admin.repository.querydsl.AdminRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static ch.qos.logback.core.util.StringUtil.*;

@RequiredArgsConstructor
public class AdminRepositoryCustomImpl implements AdminRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Admin> findAllByCondition(String name, String phone, String email, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        //        if (name != null) {
        if (notNullNorEmpty(name)) builder.and(QAdmin.admin.name.containsIgnoreCase(name));
        if (notNullNorEmpty(email)) builder.and(QAdmin.admin.email.containsIgnoreCase(email));
        if (notNullNorEmpty(phone)) builder.and(QAdmin.admin.phone.containsIgnoreCase(phone));

        List<Admin> content;
        content = jpaQueryFactory
                .select(QAdmin.admin)
                .from(QAdmin.admin)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(QAdmin.admin.count())
                .from(QAdmin.admin)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }
}
