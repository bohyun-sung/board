package com.toyproject.board.api.service.post;

import com.toyproject.board.api.config.exception.ClientException;
import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.AdminRepository;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.domain.post.repository.PostRepository;
import com.toyproject.board.api.dto.post.PostDTO;
import com.toyproject.board.api.dto.post.request.PostCreateReq;
import com.toyproject.board.api.dto.post.request.PostUpdateReq;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.utill.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostService {

    private final AdminRepository adminRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostDTO showPost(Long postIdx) {
        Post post = postRepository.findById(postIdx).orElseThrow(() -> new ClientException(ExceptionType.BAD_REQUEST, "잘못된 정보"));
        post.increaseViewCount();
        return PostDTO.from(post);
    }

    @Transactional
    public void createPost(PostCreateReq req) {
        Long currentMemberIdx = SecurityUtil.getCurrentMemberIdx();
        RoleType currentRoleType = SecurityUtil.getCurrentRoleType();

        Admin admin = adminRepository.getReferenceById(currentMemberIdx);
        Post entity = req.toEntity(admin, currentRoleType);

        postRepository.save(entity);
    }

    @Transactional
    public void updatePost(PostUpdateReq req) {
        Long currentMemberIdx = SecurityUtil.getCurrentMemberIdx();
        Post post = postRepository.findById(req.postIdx()).orElseThrow(() -> new ClientException(ExceptionType.BAD_REQUEST, "잘못된 정보"));
        // 작성자와 현재 사용자가 같은 확인
        if (!post.getAdminWriterIdx().getIdx().equals(currentMemberIdx)) {
            throw new ClientException(ExceptionType.FORBIDDEN, "권한이 없습니다.");
        }
        post.update(req.title(), req.content(), req.boardType());
    }

    @Transactional
    public void deletePost(Long postIdx) {
        Post post = postRepository.findById(postIdx).orElseThrow(() -> new ClientException(ExceptionType.BAD_REQUEST, String.format("[%s] 게시물을 찾을 수 없습니다.", postIdx)));
//        Long currentMemberIdx = SecurityUtil.getCurrentMemberIdx();

//        if (post.getRoleType().equals(RoleType.USER)) {
//            //TODO 유저 정보 확인 로직후 삭제 로직
//        }
        postRepository.delete(post);
    }
}
