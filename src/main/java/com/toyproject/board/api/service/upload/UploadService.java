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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UploadService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UploadsRepository uploadsRepository;

    /**
     * 파라미터로 받은 uploadIdxs uploads에 매핑
     * Redis upload file owner key 확인후 delete
     * @param uploadIdxs        업로드 idxs
     * @param uploadMappingIdx  uploadType 과 함께 복합키인 mapping 될 idx
     * @param userIdx           사용자 idx [owner and admin]
     * @param roleType          현재 사용자 권한
     * @param uploadType        uploadMappingIdx 과 함께 복합키인 mapping 될 type
     */
    @Transactional
    public void confirmMapping(List<Long> uploadIdxs, Long uploadMappingIdx, Long userIdx, RoleType roleType, UploadType uploadType) {
        if (uploadIdxs == null || uploadIdxs.isEmpty()) return;

        List<Uploads> uploadsList = uploadsRepository.findAllById(uploadIdxs);
        List<String> keysToDelete = new ArrayList<>();

        String currentRequester = roleType.name() + ":" + userIdx;

        for (Uploads uploads : uploadsList) {
            // Redis 소유권 대조
            String key = RedisConstants.UPLOAD_OWNER_KEY + uploads.getIdx();
            String ownerId = redisTemplate.opsForValue().get(key);

            if (ownerId == null || !ownerId.equals(currentRequester)) {
                throw new ClientException(ExceptionType.FORBIDDEN_UPLOAD_TIME_OUT);
            }

            uploads.confirmMappingIdx(uploadMappingIdx, uploadType);
            keysToDelete.add(key);
        }
            // 매핑 성공 후 redis 키 삭제
        if (!keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
        }
    }

    /**
     * 업로드 파일 수정 old data delete new data mapping
     * [old] uploadMappingIdx clear -> [new] uploadMappingIdx match
     * @param newUploadIdxs     새로운 업로드 idxs
     * @param uploadMappingIdx  uploadType 과 함께 복합키인 mapping 될 idx
     * @param userIdx           사용자 idx [owner and admin]
     * @param roleType          현재 사용자 권한
     * @param uploadType        uploadMappingIdx 과 함께 복합키인 mapping 될 type
     */
    @Transactional
    public void updateMapping(List<Long> newUploadIdxs, Long uploadMappingIdx, Long userIdx, RoleType roleType, UploadType uploadType) {
        if (newUploadIdxs == null || newUploadIdxs.isEmpty()) return;
        // 기존 매핑 되어 있는 업로드 파일 목록 조회
        List<Uploads> currentUploads = uploadsRepository.findAllByUploadMappingIdxAndUploadType(uploadMappingIdx, uploadType);
        List<Long> currentIdxs = currentUploads.stream().map(Uploads::getIdx).toList();

        // 매핑 해지할 기존 uploads 데이터 추출
        List<Uploads> toRemoveUploads = currentUploads.stream()
                .filter(oldUploads -> !newUploadIdxs.contains(oldUploads.getIdx()))
                .toList();

        // toRemoveUploads 매핑해지
        toRemoveUploads.forEach(Uploads::clearMapping);

        List<Long> toAddUploadIdxs = newUploadIdxs.stream()
                .filter(newUploadIdx -> !currentIdxs.contains(newUploadIdx))
                .toList();

        if (!toAddUploadIdxs.isEmpty()) {
            confirmMapping(toAddUploadIdxs, uploadMappingIdx, userIdx, roleType, uploadType);
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

    /**
     * 타켓 idx 와 uploadType에 일치하는 DB 데이터 uploadMoppingIdx = null 로 변경
     * @param targetIdx  타겟 idx
     * @param uploadType 업로드 타입
     */
    public void clearMapping(Long targetIdx, UploadType uploadType) {
        if (uploadType == UploadType.POST) {
            uploadsRepository.bulkClearMapping(targetIdx, uploadType);
            // 댓글 업로드 매핑 clear
            uploadsRepository.clearAllCommentFilesByPostIdx(targetIdx, UploadType.COMMENT);
        } else {
            uploadsRepository.bulkClearMapping(targetIdx, uploadType);
        }

    }
}
