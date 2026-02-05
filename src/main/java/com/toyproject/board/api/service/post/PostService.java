package com.toyproject.board.api.service.post;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.AdminRepository;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.domain.post.repository.PostRepository;
import com.toyproject.board.api.dto.post.PostDTO;
import com.toyproject.board.api.dto.post.request.PostCreateReq;
import com.toyproject.board.api.enums.coverter.RoleType;
import com.toyproject.board.api.utill.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostService {

    private final AdminRepository adminRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostDTO showPost(Long postIdx) throws BadRequestException {
        // TODO: 커스텀 exception 생성후 변경
        Post post = postRepository.findById(postIdx).orElseThrow(() -> new BadRequestException("잘못된 정보"));
        post.increaseViewCount();
        return PostDTO.from(post);
    }

    @Transactional
    public void createPost(PostCreateReq req) throws BadRequestException {
        Long currentMemberIdx = SecurityUtil.getCurrentMemberIdx();
        RoleType currentRoleType = SecurityUtil.getCurrentRoleType();

        Admin admin = adminRepository.getReferenceById(currentMemberIdx);
        Post entity = req.toEntity(admin, currentRoleType);

        postRepository.save(entity);
    }

}
