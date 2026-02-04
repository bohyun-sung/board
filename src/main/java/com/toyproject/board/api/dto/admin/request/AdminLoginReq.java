package com.toyproject.board.api.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record AdminLoginReq(
        @Schema(example = "admin_5")
        String userId,
        @Schema(example = "123!")
        String password
) {

}
