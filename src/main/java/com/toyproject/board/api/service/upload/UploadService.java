package com.toyproject.board.api.service.upload;

import com.toyproject.board.api.constants.RedisConstants;
import com.toyproject.board.api.domain.upload.entity.Uploads;
import com.toyproject.board.api.domain.upload.repository.UploadsRepository;
import com.toyproject.board.api.dto.upload.UploadsShowDto;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.enums.UploadType;
import com.toyproject.board.api.exception.ClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UploadService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UploadsRepository uploadsRepository;

    @Transactional
    public void confirmMapping(List<Long> uploadIdxs, Long uploadMappingIdx, Long userIdx, RoleType roleType, UploadType uploadType) {
        if (uploadIdxs == null || uploadIdxs.isEmpty()) return;

        List<Uploads> uploadsList = uploadsRepository.findAllById(uploadIdxs);

        for (Uploads uploads : uploadsList) {
            // Redis 소유권 대조
            String key = RedisConstants.UPLOAD_OWNER_KEY + uploads.getIdx();
            String ownerId = redisTemplate.opsForValue().get(key);

            String currentRequester = roleType.name() + ":" + userIdx;

            if (ownerId == null || !ownerId.equals(currentRequester)) {
                throw new ClientException(ExceptionType.FORBIDDEN_UPLOAD_TIME_OUT);
            }

            uploads.confirmMappingIdx(uploadMappingIdx, uploadType);
            // 매핑 성공 후 redis 키 삭제
            redisTemplate.delete(key);
        }
    }

    public List<UploadsShowDto> findAllByUploadMapping(Long uploadMappingIdx, UploadType uploadType) {

        List<Uploads> uploadsList = uploadsRepository.findAllByUploadMappingIdxAndUploadTypeOrderBySortOrderAsc(uploadMappingIdx, uploadType);

        return uploadsList.stream()
                .map(UploadsShowDto::from)
                .toList();
    }

    /**
     * Redis에 게시물 등록자 정보 저장
     * @param uploadIdx 업로드 idx
     * @param userIdx   유저 idx
     * @param roleType  유저 ROLE [ADMIN, USER]
     */
    public void saveUploadOwner(Long uploadIdx, Long userIdx, RoleType roleType) {
        String key = RedisConstants.UPLOAD_OWNER_KEY + uploadIdx;
        String value = roleType.name() + ":" + userIdx;
        redisTemplate.opsForValue().set(key, value, RedisConstants.UPLOAD_OWNER_TTL);
    }
}
