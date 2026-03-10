package com.toyproject.board.api.annotations.swagger;

import com.toyproject.board.api.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

@BadRequestErrorResponse
@UnauthorizedErrorResponse
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                        examples = @ExampleObject(value = "{ \"result\": false, \"fail\": { \"code\": \"500\", \"message\": \"서버 오류가 발생했습니다\" } }")))
})

public @interface ApiCommonResponses {
}
