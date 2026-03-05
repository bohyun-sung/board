package com.toyproject.board.api.service.post;

import com.toyproject.board.api.utill.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

import static com.toyproject.board.api.constants.RedisConstants.USER_VIEW_CHECK_KEY;
import static com.toyproject.board.api.constants.RedisConstants.VIEW_COUNT_KEY;

@Service
@RequiredArgsConstructor
public class ViewCountService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 게시물 조회수 Redis에 저장
     *
     * @param postIdx 게시물 idx
     * @param request ip저장을 위한 request
     */
    public void increaseViewCountAsync(Long postIdx, HttpServletRequest request) {
        String clientIdentifier = getClientIdentifier(request);

        String countKey = VIEW_COUNT_KEY + postIdx;
        String userCheckKey = USER_VIEW_CHECK_KEY + postIdx + ":" + clientIdentifier;

        // 키가 없을때 조회수 증가
        Boolean isView = redisTemplate.opsForValue()
                .setIfAbsent(userCheckKey, "true", Duration.ofHours(24));
        if (Boolean.TRUE.equals(isView)) {
            redisTemplate.opsForValue().increment(countKey);
        }
    }

    /**
     * Redis에 저장된 특정 게시물 실시간 추가 조회수 가져오기
     *
     * @param postIdx 게시물 idx
     * @return Redis에 저장된 조회수
     */
    public Long getViewCount(Long postIdx) {
        String countKey = VIEW_COUNT_KEY + postIdx;
        String value = redisTemplate.opsForValue().get(countKey);

        return (value != null) ? Long.parseLong(value) : 0L;
    }

    /**
     * 고유 식별자 생성
     *
     * @return 로그인 ID 우선, 미로그인 시 IP+UA 조합
     */
    private String getClientIdentifier(HttpServletRequest request) {
        Long userIdx = SecurityUtil.getOptionalCurrentIdx();

        // 로그인 사용자라면 확실한 식별자 반환
        if (userIdx != null) {
            return "USER:" + userIdx;
        }

        // 비로그인 사용자: IP와 User-Agent 조합
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        // 정보가 누락되었을 경우를 대비한 기본값 처리
        String rawId = (ip != null ? ip : "unknown") + (userAgent != null ? userAgent : "anonymous");

        // 고유하고 짧은 해시값 생성
        return "GUEST:" + hashIdentifier(rawId);
    }

    /**
     * 프록시 환경을 고려한 실제 클라이언트 IP 추출
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip)) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * 식별자 해싱 (SHA-256 활용 - 고정 길이 및 낮은 충돌률)
     */
    private String hashIdentifier(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            // 앞의 12자만 사용하여 Redis 키 길이 최적화
            for (int i = 0; i < 6; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(input.hashCode());
        }
    }
}
