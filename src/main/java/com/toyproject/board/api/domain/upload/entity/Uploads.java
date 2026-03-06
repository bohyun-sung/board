package com.toyproject.board.api.domain.upload.entity;

import com.toyproject.board.api.domain.base.DefaultTimeStampEntity;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.UploadType;
import com.toyproject.board.api.exception.ClientException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "uploads")
@Entity
public class Uploads extends DefaultTimeStampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "upload_idx", columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "upload_url", nullable = false, length = 200)
    private String uploadUrl;

    @Column(name = "thumbnail_url", nullable = false, length = 200)
    private String thumbnailUrl;

    @Column(name = "upload_type", nullable = false)
    @Convert(converter = UploadType.Converter.class)
    private UploadType uploadType;

    @Column(name = "upload_mapping_idx")
    private Long uploadMappingIdx;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "extension")
    private String extension;

    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * 업로드 생성자
     *
     * @param uploadUrl    업로드 url
     * @param thumbnailUrl 썸네일 url
     * @param uploadType   업로드 타입
     * @param fileSize     파일 사이즈
     * @param extension    확장자
     */
    public Uploads(String uploadUrl, String thumbnailUrl, UploadType uploadType, Long fileSize, String extension, Integer sortOrder) {
        this.uploadUrl = uploadUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.uploadType = uploadType;
        this.fileSize = fileSize;
        this.extension = extension;
        this.sortOrder = sortOrder;
    }

    public static Uploads of(String uploadUrl, String thumbnailUrl, UploadType uploadType, Long fileSize, String extension, Integer sortOrder) {
        return new Uploads(uploadUrl, thumbnailUrl, uploadType, fileSize, extension, sortOrder);
    }

    /**
     * uploadMappingIdx 매핑 (더티 체킹)
     */
    public void confirmMappingIdx(Long uploadMappingIdx, UploadType uploadType) {
        if (this.uploadType != uploadType) {
            throw new ClientException(ExceptionType.BAD_REQUEST, "업로드 타입이 일치하지 않습니다.");
        }

        // 이미 매핑된 데이터인지 확인
        if (this.uploadMappingIdx != null) {
            throw new ClientException(ExceptionType.BAD_REQUEST, "이미 사용 중인 파일입니다.");
        }
        this.uploadMappingIdx = uploadMappingIdx;
    }

    public void clearMapping() {
        this.uploadMappingIdx = null;
    }
}
