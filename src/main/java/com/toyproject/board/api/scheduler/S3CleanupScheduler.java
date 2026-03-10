package com.toyproject.board.api.scheduler;

import com.toyproject.board.api.domain.upload.entity.Uploads;
import com.toyproject.board.api.domain.upload.repository.UploadsRepository;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3CleanupScheduler {

    private final UploadsRepository uploadsRepository;
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Scheduled(cron = "0 10 * * * *")
    @Transactional
    public void cleanupOrphanedUploads() {
        // [TEST] 10분 전 데이터 중 매핑 안 된 데이터 조회
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        List<Uploads> orphansUploads = uploadsRepository.findAllByUploadMappingIdxIsNullAndRgdtBefore(threshold);

        if (orphansUploads.isEmpty()) return;

        log.info(">>>>> [cleanupOrphanedUploads] Target files found: {}", orphansUploads.size());

        // 삭제할 Key 리스트 생성
        List<ObjectIdentifier> keysToDelete = new ArrayList<>();
        for (Uploads upload : orphansUploads) {
            if (upload.getUploadUrl() != null) {
                keysToDelete.add(ObjectIdentifier.builder().key(extractS3Key(upload.getUploadUrl())).build());
            }
            if (upload.getThumbnailUrl() != null) {
                keysToDelete.add(ObjectIdentifier.builder().key(extractS3Key(upload.getThumbnailUrl())).build());
            }
        }

        // S3 일괄 삭제 실행 (최대 1,000개씩 나누어 처리)
        try {
            // 리스트를 1000개 단위로 쪼개기 (v2 SDK 제약 조건)
            for (int i = 0; i < keysToDelete.size(); i += 1000) {
                List<ObjectIdentifier> subList = keysToDelete.subList(i, Math.min(i + 1000, keysToDelete.size()));

                DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                        .bucket(bucketName)
                        .delete(Delete.builder().objects(subList).build())
                        .build();

                s3Client.deleteObjects(deleteRequest);
            }

            // 4. S3 삭제 성공 후 DB 데이터 일괄 삭제 (Soft Delete가 아닌 Hard Delete)
            uploadsRepository.deleteAllInBatch(orphansUploads);
            log.info(">>>>> [cleanupOrphanedUploads] Successfully cleaned up {} records", orphansUploads.size());

        } catch (S3Exception e) {
            log.error(">>>>> [cleanupOrphanedUploads] AWS S3 Error: {}", e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            log.error(">>>>> [cleanupOrphanedUploads] Unexpected Error: ", e);
        }
    }

    /**
     * URL에서 S3 Key만 추출하는 유틸리티
     * 예: https://bucket.s3.region.amazonaws.com/post/image.jpg -> post/image.jpg
     */
    private String extractS3Key(String url) {
        if (url == null || !url.contains(".com/")) return url;
        return url.substring(url.lastIndexOf(".com/") + 5);
    }
}
