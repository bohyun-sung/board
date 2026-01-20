package com.toyproject.board.api.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.core.annotations.ParameterObject;

/*
  @ModelAttribute 를 사용시에는 record 사용을 지양하는게 좋다 기본적으로 setter 기반이며,
  없을때는 생성자로 찾는데 생성자 바인딩 기능중 정확하게 퀴리 스트링 키값과 필드명이 일치해야 된다.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ParameterObject
@Schema(description = "어드민 검색목록 요청 DTO")
public class AdminSearchAdminReq{

        @Schema(description = "핸드폰", example = "010-0000-0000")
        private String phone;

        @Schema(description = "이름", example = "홍길동")
        private String name;

        @Schema(description = "이메일", example = "aaaa@example.com")
        private String email;

}
