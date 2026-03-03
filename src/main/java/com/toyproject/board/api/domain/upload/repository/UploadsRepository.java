package com.toyproject.board.api.domain.upload.repository;

import com.toyproject.board.api.domain.upload.entity.Uploads;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadsRepository extends JpaRepository<Uploads, Long> {
}
