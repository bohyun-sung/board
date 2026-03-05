package com.toyproject.board.api.service.post;

import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.member.repository.MemberRepository;
import com.toyproject.board.api.exception.ClientException;
import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.AdminRepository;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.domain.post.repository.PostRepository;
import com.toyproject.board.api.domain.upload.entity.Uploads;
import com.toyproject.board.api.domain.upload.repository.UploadsRepository;
import com.toyproject.board.api.dto.post.PostDTO;
import com.toyproject.board.api.dto.post.request.PostCreateReq;
import com.toyproject.board.api.dto.post.request.PostUpdateReq;
import com.toyproject.board.api.dto.upload.UploadsShowDto;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.enums.UploadType;
import com.toyproject.board.api.service.upload.UploadService;
import com.toyproject.board.api.utill.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostService {

    private final ViewCountService viewCountService;

    private final AdminRepository adminRepository;
    private final PostRepository postRepository;
    private final UploadService uploadService;
    private final MemberRepository memberRepository;


    public PostDTO showPost(Long postIdx, HttpServletRequest request) {
        // 게시물 정보 조회
        Post post = postRepository.findById(postIdx)
                .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND_POST, new Object[]{postIdx}));
        // 게시물 조회수 증가
        try {
            viewCountService.increaseViewCountAsync(postIdx, request);
        } catch (Exception e) {
            log.error("조회수 증사 실패 postIdx: {}", postIdx, e);
        }
        // 업로드 파일 매핑
        List<UploadsShowDto> uploadsShowDtoList = uploadService.findAllByUploadMapping(postIdx, UploadType.POST);

        // DB 조회수 + Redis 실시간 합산
        long currentRedisView = viewCountService.getViewCount(postIdx);
        Integer totalViewCount = Math.toIntExact(post.getViewCount() + currentRedisView);

        return PostDTO.fromAndUploadsAndViewCount(post, uploadsShowDtoList, totalViewCount);
    }

    @Transactional
    public void createPost(PostCreateReq req, Long userIdx, RoleType roleType) {
        Admin admin = (roleType == RoleType.ADMIN)
                ? findAdmin(userIdx)
                : null;
        Member member = (roleType == RoleType.USER)
                ? findMember(userIdx)
                : null;

        Post savePost = postRepository.save(req.toEntity(admin, member, roleType));

        // 게시물 이미지가 존재 하면 업로드테이블에 매핑
        uploadService.confirmMapping(req.uploadIdxs(), savePost.getIdx(), userIdx, roleType, UploadType.POST);

    }

    @Transactional
    public void updatePost(PostUpdateReq req) {
        Long currentMemberIdx = SecurityUtil.getRequiredCurrentIdx();
        Post post = postRepository.findById(req.postIdx()).orElseThrow(() -> new ClientException(ExceptionType.BAD_REQUEST, "잘못된 정보"));
        // 작성자와 현재 사용자가 같은 확인
        if (!post.getAdmin().getIdx().equals(currentMemberIdx)) {
            throw new ClientException(ExceptionType.FORBIDDEN);
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

    private Admin findAdmin(Long idx) {
        return adminRepository.getReferenceById(idx);
    }

    private Member findMember(Long idx) {
        return memberRepository.getReferenceById(idx);
    }
}
