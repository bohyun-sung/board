package com.toyproject.board.api.jwt.filter;

import com.toyproject.board.api.config.exception.ClientException;
import com.toyproject.board.api.config.properties.JwtTokenProperty;
import com.toyproject.board.api.constants.AuthConstants;
import com.toyproject.board.api.constants.RedisConstants;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.jwt.JwtUserInfo;
import com.toyproject.board.api.jwt.entyPoint.JwtAuthenticationEntryPoint;
import com.toyproject.board.api.security.CustomUserDetailsService;
import com.toyproject.board.api.security.properties.AppSecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProperty jwtTokenProperty;
    private final CustomUserDetailsService customUserDetailsService;
    private final AppSecurityProperties appSecurityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = resolve(request);

        try {
            // 1. 토큰이 있는 경우 검증 진행
            if (StringUtils.hasText(token)) {
                // JWT 기본 검증 (서명, 만료 등)
                if (jwtTokenProperty.validateToken(token)) {

                    // 2. 블랙리스트(로그아웃 토큰) 검증
                    if (isBlacklisted(token)) {
                        log.warn("로그아웃된 토큰으로 접근 시도 발견: {}", token);
                        handleAuthenticationException(request, response, "token.logout");
                        return;
                    }

                    // 3. 인증 객체 생성 및 컨텍스트 저장
                    processAuthentication(request, token);

                    filterChain.doFilter(request, response);
                    return;
                }
                // validateToken이 false를 리턴하는 경우 (예외를 던지지 않을 때를 대비)
                throw new ClientException(ExceptionType.UNAUTHORIZED_TOKEN_INVALID);
            }

            // 4. 토큰이 없는 경우 (SecurityConfig의 설정에 따라 permitAll 혹은 EntryPoint로 흐름이 넘어감)
            filterChain.doFilter(request, response);

        } catch (ClientException e) {
            log.error("JWT Filter ClientException: {}", e.getMessage());
            handleAuthenticationException(request, response, e.getMessage());
        } catch (Exception e) {
            log.error("JWT Filter Unexpected Exception: ", e);
            handleAuthenticationException(request, response, "server.error");
        }
    }

    /**
     * 화이트리스트 경로는 필터 건너뛰기
     *
     * @param request current HTTP request
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return appSecurityProperties.getAllExcludes().stream()
                .anyMatch(patten -> pathMatcher.match(patten, path));
    }

    /**
     * Bearer 로 시작되는지 확인
     */
    private String resolve(HttpServletRequest request) {
        String bearerToken = request.getHeader(AuthConstants.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(AuthConstants.BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null; // 에러 처리
    }
    /**
     * Redis 블랙리스트 여부 확인
     */
    private boolean isBlacklisted(String token) {
        // RedisTemplate의 get 결과가 존재하면 로그아웃된 토큰임
        return redisTemplate.hasKey(RedisConstants.LOGOUT_ACCESS_TOKEN + token);
    }

    /**
     * 인증 프로세스 공통 로직 분리
     */
    private void processAuthentication(HttpServletRequest request, String token) {
        JwtUserInfo userInfo = jwtTokenProperty.getUserInfo(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUserInfo(userInfo);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 인증 실패 시 공통 예외 처리
     */
    private void handleAuthenticationException(HttpServletRequest request, HttpServletResponse response, String exceptionKey) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        request.setAttribute("exception", exceptionKey);
        jwtAuthenticationEntryPoint.commence(request, response, null);
    }
}
