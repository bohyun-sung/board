package com.toyproject.board.api.service.post;

import com.toyproject.board.api.utill.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.toyproject.board.api.constants.RedisConstants.USER_VIEW_CHECK_KEY;
import static com.toyproject.board.api.constants.RedisConstants.VIEW_COUNT_KEY;

@Service
@RequiredArgsConstructor
public class ViewCountService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 게시물 조회수 Redis에 저장
     * @param postIdx   게시물 idx
     * @param request   ip저장을 위한 request
     */
    public void increaseViewCountAsync(Long postIdx, HttpServletRequest request) {
        String clientIdentifier = getClientIdentifier(request);
        String countKey = VIEW_COUNT_KEY + postIdx;
        String userCheckKey = USER_VIEW_CHECK_KEY + postIdx + ":" + clientIdentifier;

        if (!redisTemplate.hasKey(userCheckKey)) {
            redisTemplate.opsForValue().increment(countKey);
            redisTemplate.opsForValue().set(userCheckKey, "true", Duration.ofHours(24));
        }
    }

    /**
     * Redis에 저장된 특정 게시물 실시간 추가 조회수 가져오기
     * @param postIdx 게시물 idx
     * @return Redis에 저장된 조회수
     */
    public Long getViewCount(Long postIdx) {
        String countKey = VIEW_COUNT_KEY + postIdx;
        String value = redisTemplate.opsForValue().get(countKey);

        return (value != null) ? Long.parseLong(value) : 0L;
    }

    private String getClientIdentifier(HttpServletRequest request) {
        Long memberIdx = SecurityUtil.getCurrentMemberIdxOptional();
        if (memberIdx != null) return "USER:" + memberIdx;

        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();
        return "IP:" + ip;
    }
}
