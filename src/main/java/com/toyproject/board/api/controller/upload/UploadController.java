package com.toyproject.board.api.controller.upload;

import com.toyproject.board.api.annotations.CurrentUserIdx;
import com.toyproject.board.api.annotations.CurrentUserRoleType;
import com.toyproject.board.api.config.Response;
import com.toyproject.board.api.dto.upload.response.UploadsRes;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.enums.UploadType;
import com.toyproject.board.api.service.upload.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "[03] 업로드")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uploads")
public class UploadController {

    private final S3Service s3Service;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "400", description = "허용 되지 않는 파일 형태"),
            @ApiResponse(responseCode = "500", description = "업로드 실패")
    })
    @Operation(summary = "파일 업로드", description = "bulk upload")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<List<UploadsRes>> uploadFiles(
            @RequestPart("files") List<MultipartFile> files,
            @CurrentUserIdx @Parameter(hidden = true) Long userIdx,
            @CurrentUserRoleType @Parameter(hidden = true) RoleType roleType,
            @RequestPart("uploadType") UploadType uploadType) {
        return Response.success(s3Service.uploadMultipleFiles(files, userIdx, roleType, uploadType));
    }
}
