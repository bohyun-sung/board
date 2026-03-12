package com.toyproject.board.api.dto.upload;

import com.toyproject.board.api.domain.upload.entity.Uploads;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "업로드 파일 보여주는 DTO")
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadsShowDto {
    @Schema(description = "업로드 idx")
    private Long uploadIdx;
    @Schema(description = "업로드 URL")
    private String uploadUrl;
    @Schema(description = "썸네일 URL")
    private String thumbnailUrl;
    @Schema(description = "업로드 파일 순서")
    private Integer sortOrder;
    @Schema(description = "업로드 매핑 idx")
    private Long uploadMappingIdx;

    public static UploadsShowDto from(Uploads entity) {
        return new UploadsShowDto(entity.getIdx(), entity.getUploadUrl(), entity.getThumbnailUrl(), entity.getSortOrder(), entity.getUploadMappingIdx());
    }
}
