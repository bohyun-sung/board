package com.toyproject.board.api.service.comment;

import com.toyproject.board.api.annotations.CheckOwner;
import com.toyproject.board.api.domain.comment.entity.Comment;
import com.toyproject.board.api.domain.comment.repository.CommentRepository;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.member.repository.MemberRepository;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.domain.post.repository.PostRepository;
import com.toyproject.board.api.dto.comment.request.CommentCreateReq;
import com.toyproject.board.api.dto.comment.request.CommentUpdateReq;
import com.toyproject.board.api.enums.CheckType;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.enums.UploadType;
import com.toyproject.board.api.exception.ClientException;
import com.toyproject.board.api.service.upload.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentService {

    private final UploadService uploadService;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void createComment(Long postIdx, Long userIdx, RoleType roleType, CommentCreateReq req) {
        Post post = postRepository.findById(postIdx)
                .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND_POST));

        Member member = memberRepository.findById(userIdx)
                .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND_MEMBER));

        // 부모 댓글 조회
        Comment parentComment = null;
        if (req.getParentIdx() != null) {
            parentComment = commentRepository.findById(req.getParentIdx())
                    .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND_COMMENT, new Object[]{req.getParentIdx()}));
            // 부모 댓글과 현재 댓글이 같은 게시물인지 체크
            if (!parentComment.getPost().getIdx().equals(postIdx)) {
                throw new ClientException(ExceptionType.BAD_REQUEST_INVALID_PARENT_COMMENT);
            }
        }

        Comment entity = req.toEntity(post, member, parentComment);

        Comment saveComment = commentRepository.save(entity);
        // 댓글 업로드 매핑
        uploadService.confirmMapping(req.getUploadIdxs(), saveComment.getIdx(), userIdx, roleType, UploadType.COMMENT);

    }

    @CheckOwner(type = CheckType.COMMENT)
    @Transactional
    public void updateComment(Long commentIdx, Long userIdx, RoleType roleType, CommentUpdateReq req) {
        Comment comment = commentRepository.findById(commentIdx)
                .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND_COMMENT, new Object[]{commentIdx}));
        // 삭제된 댓글인지 확인
        if (comment.isDeleted()) {
            throw new ClientException(ExceptionType.GONE_ALREADY_DELETED_COMMENT);
        }
        // 댓글 내용 수정
        comment.modifyContent(req.getContent());
        // 업로드 매핑 변경
        uploadService.updateMapping(req.getUploadIdxs(), commentIdx, userIdx, roleType, UploadType.COMMENT);
    }

    @CheckOwner(type = CheckType.COMMENT) // 작성자 또는 관리자 권한 체크 AOP
    @Transactional
    public void deleteComment(Long commentIdx, Long userIdx, RoleType roleType) {
        Comment comment = commentRepository.findById(commentIdx)
                .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND_COMMENT, new Object[]{commentIdx}));
        // 중복 삭제 방지
        if (comment.isDeleted()) {
            throw new ClientException(ExceptionType.GONE_ALREADY_DELETED_COMMENT);
        }
        // soft delete
        comment.modifyIsDeleted(true);
        comment.modifyContent("삭제된 댓글입니다");
        uploadService.clearMapping(commentIdx, UploadType.COMMENT);
    }
}
