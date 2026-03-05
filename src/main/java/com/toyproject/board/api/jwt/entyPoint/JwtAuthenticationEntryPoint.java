package com.toyproject.board.api.jwt.entyPoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toyproject.board.api.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("EntryPoint 실행됨! path: {}", request.getRequestURI());
        Object exception = request.getAttribute("exception");
        String errorMessage = (exception != null) ? exception.toString() : "인증에 실패했습니다.";

        String message;
        try {
            message = messageSource.getMessage(errorMessage, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            message = errorMessage;
        }
        // 기본값은 인증되지 않음
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);


        String result = objectMapper.writeValueAsString(new ErrorResponse(401, message));

        response.getWriter().write(result);
    }
}
