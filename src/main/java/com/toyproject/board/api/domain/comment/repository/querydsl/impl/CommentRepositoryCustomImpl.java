package com.toyproject.board.api.domain.comment.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.toyproject.board.api.domain.comment.entity.Comment;
import com.toyproject.board.api.domain.comment.repository.querydsl.CommentRepositoryCustom;
import com.toyproject.board.api.domain.upload.entity.Uploads;
import com.toyproject.board.api.dto.comment.CommentDto;
import com.toyproject.board.api.dto.upload.UploadsShowDto;
import com.toyproject.board.api.enums.UploadType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

import static com.toyproject.board.api.domain.comment.entity.QComment.comment;
import static com.toyproject.board.api.domain.upload.entity.QUploads.uploads;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<CommentDto> findByCondition(Long postIdx, Pageable pageable) {

        // 최상위 댓글
        List<Long> rootCommentIdxs = queryFactory.select(comment.idx)
                .from(comment)
                .where(
                        comment.post.idx.eq(postIdx),
                        comment.parentComment.isNull())
                .orderBy(comment.rgdt.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 최상위 댓글이 없으면 빈배열 반환
        if (rootCommentIdxs.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
        // 최상위 댓글의 대댓글 가져오기
        List<Comment> allComments = queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member).fetchJoin()
                .where(
                        comment.idx.in(rootCommentIdxs)
                                .or(comment.rootCommentIdx.in(rootCommentIdxs))
                )
                .orderBy(comment.path.asc())
                .fetch();
        // 모든 댓글 idx 추출
        List<Long> allCommentIdxs = allComments.stream().map(Comment::getIdx).toList();

        List<Uploads> allUploads = queryFactory
                .selectFrom(uploads)
                .where(
                        uploads.uploadMappingIdx.in(allCommentIdxs),
                        uploads.uploadType.eq(UploadType.COMMENT))
                .fetch();
        // 모든 업로드 파일 조회
        List<UploadsShowDto> uploadsShowDtoList = allUploads.stream()
                .map(UploadsShowDto::from)
                .toList();

        Map<Long, List<UploadsShowDto>> uploadsMap = uploadsShowDtoList.stream()
                .collect(Collectors.groupingBy(UploadsShowDto::getUploadMappingIdx));

        List<CommentDto> treeResult = convertToTree(allComments, uploadsMap);

        Long totalCount = queryFactory.select(comment.count())
                .from(comment)
                .where(comment.post.idx.eq(postIdx)
                                .and(comment.isDeleted.eq(false)))
                .fetchOne();

        return new PageImpl<>(treeResult, pageable, totalCount != null ? totalCount : 0L);
    }

    private List<CommentDto> convertToTree(List<Comment> allComments, Map<Long, List<UploadsShowDto>> uploadsMap) {
        List<CommentDto> result = new ArrayList<>();
        Map<Long, CommentDto> map = new HashMap<>();

        allComments.forEach(comment -> {
            // 현재 댓글을 DTO로 변환 하여 Map에 저장
            CommentDto dto = CommentDto.from(comment);
            // 이미지 CommentDto 에 저장
            dto.updateUploads(uploadsMap.getOrDefault(comment.getIdx(), new ArrayList<>()));

            map.put(dto.getCommentIdx(), dto);

            // 부모가 존재하면 children 리스트 추가
            if (comment.getParentComment() != null) {
                CommentDto parentDto = map.get(comment.getParentComment().getIdx());
                // 대댓글 설정
                if (parentDto != null) {
                    parentDto.getChildren().add(dto);
                }
            } else {
                result.add(dto);
            }
        });
        return result;
    }
}
