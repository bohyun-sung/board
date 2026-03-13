package com.toyproject.board.api.dto.comment.response;

import com.toyproject.board.api.dto.comment.CommentDto;
import com.toyproject.board.api.dto.upload.UploadsShowDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "댓글 목록 RES")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentListRes {
    @Schema(description = "댓글 idx")
    private Long commentIdx;
    @Schema(description = "댓글 내용")
    private String content;
    @Schema(description = "멤버 idx")
    private Long memberIdx;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "댓글 생성시간")
    private LocalDateTime rgdt;
    @Schema(description = "댓글 삭제 여부")
    private boolean isDeleted;
    @Schema(description = "게시물 업로드 파일")
    List<UploadsShowDto> uploads = new ArrayList<>();
    @Schema(description = "대댓글")
    @Builder.Default
    List<CommentListRes> children = new ArrayList<>();


    public static CommentListRes from(CommentDto dto) {
        return CommentListRes.builder()
                .commentIdx(dto.getCommentIdx())
                .content(dto.getContent())
                .memberIdx(dto.getMemberIdx())
                .nickname(dto.getNickname())
                .rgdt(dto.getRgdt())
                .isDeleted(dto.isDeleted())
                .uploads(dto.getUploads())
                .children(dto.getChildren().stream()
                        .map(CommentListRes::from)
                        .toList())
                .build();
    }

}
