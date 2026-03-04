package com.toyproject.board.api.service.upload;

import com.toyproject.board.api.config.exception.ClientException;
import com.toyproject.board.api.config.exception.ServerException;
import com.toyproject.board.api.domain.upload.entity.Uploads;
import com.toyproject.board.api.domain.upload.repository.UploadsRepository;
import com.toyproject.board.api.dto.upload.UploadsDto;
import com.toyproject.board.api.dto.upload.response.UploadsRes;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.UploadType;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    private final UploadsRepository uploadsRepository;

    private static final String DATE_FORMAT = "yyyyMMddHHmmss";

    private static final List<String> ALLOWED_EXTENSIONS =
            List.of("jpg", "jpeg", "png", "gif", "pdf", "docx", "txt");

    // 개별 파일 최대 용량 (예: 10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;


    /**
     * 다중 업로드
     *
     * @param files 업로드 파일
     * @return 업로드 URL
     */
    @Transactional
    public List<UploadsRes> uploadMultipleFiles(List<MultipartFile> files, UploadType uploadType) {
        if (files == null || files.isEmpty()) return Collections.emptyList();

        // 0~파일사이즈까지 루프 하여 순서 업로드
        Stream<UploadsDto> uploadsDtoStream = IntStream.range(0, files.size())
                .parallel()
                .mapToObj(sortOrder -> {
                    MultipartFile file = files.get(sortOrder);
                    return upload(file, uploadType, sortOrder);
                });

        // 이미지 데이터 저장
        List<Uploads> uploadsList = uploadsDtoStream.map(UploadsDto::toEntity).toList();

        List<Uploads> uploadsSaveData = uploadsRepository.saveAll(uploadsList);

        return uploadsSaveData.stream()
                .map(UploadsRes::from)
                .toList();


    }

    /**
     * 업로드 파일 삭제
     *
     * @param fileUrls 업로드 파일 url
     */
    public void deleteMultipleFiles(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) return;

        fileUrls.parallelStream()
                .forEach(this::deleteFile);
    }

    /**
     * 파일 업로드
     *
     * @param file 업로드 파일
     * @return 업로드된 url
     */
    private UploadsDto upload(MultipartFile file, UploadType uploadType, int sortOrder) {
        validateFile(file);

        String originalName = Normalizer.normalize(Objects.requireNonNull(file.getOriginalFilename()), Normalizer.Form.NFC);
        String fileName = generateS3Key(originalName);

        try (InputStream inputStream = file.getInputStream()) {
            S3Resource s3Resource = s3Template.upload(bucketName, fileName, inputStream);
            log.debug("Successfully uploaded to S3: {}", fileName);

            return UploadsDto.builder()
                    .uploadUrl(s3Resource.getURL().toString())
                    .thumbnailUrl(uploadThumbnail(file))
                    .uploadType(uploadType)
                    .fileSize(file.getSize())
                    .extension(StringUtils.getFilenameExtension(file.getOriginalFilename()))
                    .sortOrder(sortOrder)
                    .build();

        } catch (IOException e) {
            log.error("S3 upload failed for file: {}", originalName, e);
            throw new ServerException(ExceptionType.INTERNAL_SERVER_ERROR,
                    String.format("파일 업로드 중 오류 발생: [%s]", originalName));
        }
    }


    /**
     * 업로드 파일 삭제
     *
     * @param fileUrl 파일 url
     */
    private void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            s3Template.deleteObject(bucketName, key);
            log.info("Successfully deleted from S3: {}", key);
        } catch (Exception e) {
            log.warn("Failed to delete S3 object: {}", fileUrl, e);
        }
    }

    /**
     * S3에 저장 될 파일명 생성
     * timestamp/UUID/cleanedName.extension
     *
     * @param originalFilename 업로드 파일명
     * @return S3에 저장될 신규 key
     */
    private String generateS3Key(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String baseName = StringUtils.stripFilenameExtension(originalFilename);

        String cleanedName = Normalizer.normalize(baseName, Normalizer.Form.NFC)
                .replaceAll("[^a-zA-Z0-9가-힣]", "_");

        // 2. 타임스탬프 + UUID 조합
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        return String.format("%s/%s_%s/%s.%s", "uploads", timestamp, UUID.randomUUID().toString().substring(0, 8), cleanedName, extension);
    }

    /**
     * URL path 정리
     *
     * @param fileUrl 업로드 파일 URL
     * @return 도메인 제거된 PATH
     */
    private String extractKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (MalformedURLException e) {
            return fileUrl;
        }
    }

    /**
     * 파일 검증 (확장자, 용량)
     *
     * @param file 업로드 파일
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ClientException(ExceptionType.BAD_REQUEST, "빈 파일은 업로드할 수 없습니다");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ClientException(ExceptionType.BAD_REQUEST,
                    String.format("파일 용량이 제한(10MB)을 초과했습니다 (현재: %d bytes)", file.getSize()));
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new ClientException(ExceptionType.BAD_REQUEST, "허용되지않는 파일 형식입니다.");
        }
    }

    /**
     * 업로드 파일 썸네일 생성
     * @param file 업로드 파일
     * @return 썸내일 url
     */
    private String uploadThumbnail(MultipartFile file) {
        String thumbName = "s_" + generateS3Key(file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){

            Thumbnails.of(inputStream)
                    .size(200, 200)
                    .outputFormat("jpg")
                    .toOutputStream(byteArrayOutputStream);

            byte[] bytes = byteArrayOutputStream.toByteArray();
            try (ByteArrayInputStream thumbInputStream = new ByteArrayInputStream(bytes)){
                S3Resource s3Resource = s3Template.upload(bucketName, thumbName, thumbInputStream);
                return s3Resource.getURL().toString();
            }
        } catch (IOException e) {
            log.error("썸네일 생성 실패", e);
            return null;
        }
    }
}
