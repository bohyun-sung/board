package com.toyproject.board.api.domain.post.repository.querydsl.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.toyproject.board.api.domain.post.repository.querydsl.PostRepositoryCustom;
import com.toyproject.board.api.dto.post.PostListDto;
import com.toyproject.board.api.dto.post.QPostListDto;
import com.toyproject.board.api.enums.BoardType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.toyproject.board.api.domain.member.entity.QMember.member;
import static com.toyproject.board.api.domain.post.entity.QPost.post;
import static org.springframework.util.StringUtils.hasText;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostListDto> findAllByCondition(String title, String content, String nickname, BoardType boardType, Pageable pageable) {

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
                .where(
                        eqTitle(title),
                        containsContent(content),
                        eqMemberNickname(nickname),
                        eqBoardType(boardType))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.rgdt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .leftJoin(post.member, member)
                .where(
                        eqTitle(title),
                        containsContent(content),
                        eqMemberNickname(nickname),
                        eqBoardType(boardType));

        return PageableExecutionUtils.getPage(fetch, pageable, countQuery::fetchOne);
    }

    private BooleanExpression eqTitle(String title) {
        return hasText(title) ? post.title.eq(title) : null;
    }

    private BooleanExpression eqMemberNickname(String nickname) {
        return hasText(nickname) ? member.nickname.eq(nickname) : null;
    }

    private BooleanExpression containsContent(String content) {
        return hasText(content) ? post.content.containsIgnoreCase(content) : null;
    }

    private BooleanExpression eqBoardType(BoardType boardType) {
        return boardType != null ? post.boardType.eq(boardType) : null;
    }
}
