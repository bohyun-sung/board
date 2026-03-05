package com.toyproject.board.api.service.post;

import com.toyproject.board.api.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ViewCountSyncService {

    private final PostRepository postRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void syncEachViewCount(Long postIdx, Long viewCount) {
        postRepository.updateViewCount(postIdx, viewCount);
    }
}
