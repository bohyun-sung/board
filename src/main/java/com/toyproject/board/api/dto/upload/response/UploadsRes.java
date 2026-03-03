package com.toyproject.board.api.dto.upload.response;

import com.toyproject.board.api.domain.upload.entity.Uploads;
import com.toyproject.board.api.enums.UploadType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "업로드 RES")
public record UploadsRes(
        @Schema(description = "업로드 idx", example = "")
        Long uploadIdx,
        @Schema(description = "업로드파일 URL", example = "")
        String uploadUrl,
        @Schema(description = "썸네일 URL", example = "")
        String thumbnailUrl,
        @Schema(description = "업로드 타입", example = "0: 게시판")
        UploadType uploadType
) {
    public static UploadsRes from(Uploads entity) {
        return new UploadsRes(entity.getIdx(), entity.getUploadUrl(), entity.getThumbnailUrl(), entity.getUploadType());
    }
}
