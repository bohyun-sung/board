package com.toyproject.board.api.dto.upload;

import com.toyproject.board.api.domain.upload.entity.Uploads;
import com.toyproject.board.api.enums.UploadType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadsDto {
    private Long uploadIdx;
    private String uploadUrl;
    private String thumbnailUrl;
    private UploadType uploadType;
    private Long uploadMappingIdx;
    private Long fileSize;
    private String extension;

    public Uploads toEntity() {
        return Uploads.of(uploadUrl, thumbnailUrl, uploadType, fileSize, extension);
    }
}
