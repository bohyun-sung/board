package com.toyproject.board.api.domain.upload.repository;

import com.toyproject.board.api.domain.upload.entity.Uploads;
import com.toyproject.board.api.enums.UploadType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface UploadsRepository extends JpaRepository<Uploads, Long> {

    List<Uploads> findAllByUploadMappingIdxAndUploadTypeOrderBySortOrderAsc(Long uploadMappingIdx, UploadType uploadType);

    List<Uploads> findAllByUploadMappingIdxAndUploadType(Long uploadMappingIdx, UploadType uploadType);

    List<Uploads> findAllByUploadMappingIdxIsNullAndRgdtBefore(LocalDateTime threshold);
}
