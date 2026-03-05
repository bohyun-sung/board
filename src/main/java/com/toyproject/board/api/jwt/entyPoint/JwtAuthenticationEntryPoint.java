package com.toyproject.board.api.jwt.entyPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyproject.board.api.config.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Object exception = request.getAttribute("exception");

        // 기본값은 인증되지 않음
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorMessage = (exception != null) ? exception.toString() : "인증에 실패했습니다.";

        String result = objectMapper.writeValueAsString(new ErrorResponse(401, errorMessage));

        response.getWriter().write(result);
    }
}
