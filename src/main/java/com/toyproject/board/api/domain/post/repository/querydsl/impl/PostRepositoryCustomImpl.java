package com.toyproject.board.api.domain.post.repository.querydsl.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.toyproject.board.api.domain.post.repository.querydsl.PostRepositoryCustom;
import com.toyproject.board.api.dto.post.PostListDto;
import com.toyproject.board.api.dto.post.QPostListDto;
import com.toyproject.board.api.enums.BoardType;
import com.toyproject.board.api.utill.QueryDslWhereBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.toyproject.board.api.domain.member.entity.QMember.member;
import static com.toyproject.board.api.domain.post.entity.QPost.post;
import static com.toyproject.board.api.utill.QueryDslUtil.getSafeOrderSpecifier;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostListDto> findAllByCondition(String title, String content, String nickname, BoardType boardType, Pageable pageable) {

        BooleanBuilder whereBuilder = QueryDslWhereBuilder.init()
                .andEq(post.title, title)
                .andContains(post.content, content)
                .andEq(member.nickname, nickname)
                .andEq(post.boardType, boardType)
                .build();

        Map<String, Expression<?>> sortMap = new HashMap<>();
        sortMap.put("rgdt", post.rgdt);
        sortMap.put("postIdx", post.idx);

        OrderSpecifier<?>[] orders = getSafeOrderSpecifier(pageable.getSort(), sortMap, post.idx.desc());
        List<PostListDto> fetch = queryFactory.select(new QPostListDto(
                        post.idx,
                        post.title,
                        post.boardType,
                        post.viewCount,
                        post.roleType,
                        post.rgdt,
                        member.nickname))
                .from(post)
                .leftJoin(post.member, member)
                .where(whereBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orders)
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(post.member, member)
                .where(whereBuilder);

        return PageableExecutionUtils.getPage(fetch, pageable, countQuery::fetchOne);
    }

}
