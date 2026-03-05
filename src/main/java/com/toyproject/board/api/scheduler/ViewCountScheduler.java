package com.toyproject.board.api.scheduler;

import com.toyproject.board.api.constants.RedisConstants;
import com.toyproject.board.api.domain.post.repository.PostRepository;
import com.toyproject.board.api.service.post.ViewCountSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.toyproject.board.api.constants.RedisConstants.VIEW_COUNT_KEY;

@Slf4j
@RequiredArgsConstructor
@Component
public class ViewCountScheduler {
    private final RedisTemplate<String, String> redisTemplate;
    private final ViewCountSyncService viewCountSyncService;

    @Transactional
    @Scheduled(cron = "0 0/30 * * * *") // 30분마다 DB 업데이트
    public void syncViewCountToDb() {
        Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY + "*");
        if (keys.isEmpty()) return;
        log.info(">>>>>>> [syncViewCountToDb] Start - count: {}", keys.size());
        for (String key : keys) {
            try {
                Long postIdx = Long.parseLong(key.replace(VIEW_COUNT_KEY, ""));
                String value = redisTemplate.opsForValue().get(key);
                if (value != null) {
                    Long viewCount = Long.parseLong(value);
                    // DB 업데이트 트랜잭션 전파 단계 새로운 트랜잭션 생성
                    viewCountSyncService.syncEachViewCount(postIdx, viewCount);
                    // Redis 해당 키 삭제
                    redisTemplate.delete(key);
                }
            } catch (Exception e) {
                log.error(">>>>>>> [syncViewCountToDb] Fail Key: {}, Error: {}", key, e.getMessage());
            }
        }
        log.info(">>>>>>> [syncViewCountToDb] Complete");
    }
}
