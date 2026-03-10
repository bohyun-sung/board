package com.toyproject.board.api.service.post;

import com.toyproject.board.api.annotations.CheckOwner;
import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.AdminRepository;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.member.repository.MemberRepository;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.domain.post.repository.PostRepository;
import com.toyproject.board.api.dto.post.PostDTO;
import com.toyproject.board.api.dto.post.PostListDto;
import com.toyproject.board.api.dto.post.request.PostCreateReq;
import com.toyproject.board.api.dto.post.request.PostListReq;
import com.toyproject.board.api.dto.post.request.PostUpdateReq;
import com.toyproject.board.api.dto.upload.UploadsShowDto;
import com.toyproject.board.api.enums.CheckType;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.enums.UploadType;
import com.toyproject.board.api.exception.ClientException;
import com.toyproject.board.api.service.upload.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostService {

    private final ViewCountService viewCountService;
    private final UploadService uploadService;

    private final AdminRepository adminRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public Page<PostListDto> getPostList(PostListReq req, Pageable pageable) {
        // 게시물 목록 조회
        Page<PostListDto> posts = postRepository.findAllByCondition(req.title(), req.content(), req.nickname(), req.boardType(), pageable);

        if (posts.isEmpty()) return posts;

        List<Long> postIdxs = posts.getContent().stream()
                .map(PostListDto::getPostIdx)
                .toList();

        List<String> listRedisViewCount = viewCountService.getListViewCount(postIdxs);
        // 게시물 조회수 실시간성 제공을 위해 DB + redis 조회수 합산
        for (int i = 0; i < posts.getContent().size(); i++) {
            PostListDto postListDto = posts.getContent().get(i);
            String value = (listRedisViewCount != null && listRedisViewCount.get(i) != null)
                    ? listRedisViewCount.get(i)
                    : "0";

            int totalViewCount = postListDto.getViewCount() + Integer.parseInt(value);

            postListDto.updateViewCount(totalViewCount);
        }
        return posts;
    }


    public PostDTO showPost(Long postIdx, HttpServletRequest request) {
        // 게시물 정보 조회
        Post post = postRepository.findById(postIdx)
                .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND_POST, new Object[]{postIdx}));
        // 게시물 조회수 증가
        try {
            viewCountService.increaseViewCountAsync(postIdx, request);
        } catch (Exception e) {
            log.error("Failed to increase views postIdx: {}", postIdx, e);
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
                ? adminRepository.getReferenceById(userIdx)
                : null;
        Member member = (roleType == RoleType.USER)
                ? memberRepository.getReferenceById(userIdx)
                : null;

        Post savePost = postRepository.save(req.toEntity(admin, member, roleType));

        // 게시물 이미지가 존재 하면 업로드테이블에 매핑
        uploadService.confirmMapping(req.uploadIdxs(), savePost.getIdx(), userIdx, roleType, UploadType.POST);

    }

    @CheckOwner(type = CheckType.POST)
    @Transactional
    public void updatePost(Long postIdx, Long userIdx, RoleType roleType, PostUpdateReq req) {

        Post post = postRepository.findById(postIdx)
                .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND_POST));

        post.update(req.title(), req.content(), req.boardType());

        uploadService.updateMapping(req.uploadIdxs(), postIdx, userIdx, roleType, UploadType.POST);

    }

    @CheckOwner(type = CheckType.POST)
    @Transactional
    public void deletePost(Long postIdx, Long userIdx, RoleType roleType) {
        Post post = postRepository.findById(postIdx)
                .orElseThrow(() -> new ClientException(ExceptionType.BAD_REQUEST, String.format("[%s] 게시물을 찾을 수 없습니다.", postIdx)));

        uploadService.clearMapping(postIdx, UploadType.POST);

        postRepository.delete(post);
        log.info("Post delete by {} roleType {}: postIdx {}", userIdx, roleType, postIdx);
    }

}
