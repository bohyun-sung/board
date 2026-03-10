package com.toyproject.board.api.domain.upload.repository;

import com.toyproject.board.api.domain.upload.entity.Uploads;
import com.toyproject.board.api.enums.UploadType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface UploadsRepository extends JpaRepository<Uploads, Long> {

    List<Uploads> findAllByUploadMappingIdxAndUploadTypeOrderBySortOrderAsc(Long uploadMappingIdx, UploadType uploadType);

    List<Uploads> findAllByUploadMappingIdxAndUploadType(Long uploadMappingIdx, UploadType uploadType);

    List<Uploads> findAllByUploadMappingIdxIsNullAndRgdtBefore(LocalDateTime threshold);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Uploads u SET u.uploadMappingIdx = null WHERE u.uploadMappingIdx = :targetIdx AND u.uploadType = :uploadType")
    void bulkClearMapping(@Param("targetIdx") Long targetIdx, @Param("uploadType") UploadType uploadType);
}
