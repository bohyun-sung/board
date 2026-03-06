package com.toyproject.board.api.aspect;

import com.toyproject.board.api.annotations.CheckOwner;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.domain.post.repository.PostRepository;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
public class CheckOwnerAspect {

    private final PostRepository postRepository;

    @Before("@annotation(checkOwner) && args(targetIdx, userIdx, roleType, ..)")
    public void checkOwner(CheckOwner checkOwner, Long postIdx, Long userIdx, RoleType roleType) {
        // 관리자면 수정 삭제 가능
        if (roleType == RoleType.ADMIN) {
            return;
        }
        Long ownerIdx = switch (checkOwner.type()) {
            case POST -> postRepository.findById(postIdx)
                    .map(post -> post.getMember().getIdx())
                    .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND_POST));
            case COMMENT -> null;
        };


        if (ownerIdx == null || !ownerIdx.equals(userIdx)) {
            throw new ClientException(ExceptionType.FORBIDDEN);
        }
    }
}
