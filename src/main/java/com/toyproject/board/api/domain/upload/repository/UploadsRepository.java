package com.toyproject.board.api.domain.upload.repository;

import com.toyproject.board.api.domain.upload.entity.Uploads;
import com.toyproject.board.api.enums.UploadType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadsRepository extends JpaRepository<Uploads, Long> {
    List<Uploads> findAllByUploadMappingIdxAndUploadTypeOrderBySortOrderAsc(Long idx, UploadType uploadType);
}
