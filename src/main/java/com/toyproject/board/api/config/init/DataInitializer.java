package com.toyproject.board.api.config.init;

import com.toyproject.board.api.domain.comment.entity.Comment;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.member.repository.MemberRepository;
import com.toyproject.board.api.domain.post.entity.Post;
import com.toyproject.board.api.domain.post.repository.PostRepository;
import com.toyproject.board.api.enums.BoardType;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile({"dev"})
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Override
    public void run(String... args) throws Exception {
        if (postRepository.count() > 100) {
            return;
        }
        Member member = memberRepository.findById(3L)
                .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND_MEMBER, new Object[4]));
        List<Post> postList = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            String title = "게시글" + i + "번글입니다";
            String content = "기능을 테스트하기 위한 샘플 데이터입니다";
            BoardType boardType = BoardType.FREE;
            RoleType roleType = RoleType.USER;
            Post post = Post.of(title, content, boardType, roleType, null, member);

            postList.add(post);
        }
        if (!postList.isEmpty()) {
            postRepository.saveAll(postList);
        }

    }
}
