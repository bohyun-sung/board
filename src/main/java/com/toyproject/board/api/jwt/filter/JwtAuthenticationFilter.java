package com.toyproject.board.api.jwt.filter;

import com.toyproject.board.api.config.exception.ClientException;
import com.toyproject.board.api.config.properties.JwtTokenProperty;
import com.toyproject.board.api.constants.AuthConstants;
import com.toyproject.board.api.jwt.JwtUserInfo;
import com.toyproject.board.api.security.CustomUserDetailsService;
import com.toyproject.board.api.security.properties.AppSecurityProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProperty jwtTokenProperty;
    private final CustomUserDetailsService customUserDetailsService;
    private final AppSecurityProperties appSecurityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = resolve(request);

            if (StringUtils.hasText(token)) {
                if (jwtTokenProperty.validateToken(token)) {
                    // 토큰에서 식별자 추출
                    JwtUserInfo userInfo = jwtTokenProperty.getUserInfo(token);
                    // ADMIN 또는 MEMBER 조회
                    UserDetails userDetails = customUserDetailsService.loadUserByUserInfo(userInfo);
                    // 인증 객체 생성 및 권한 및 정보 설정
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // 요청된 상세 정보를 인증 객체에 바인딩
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // SecurityContext에 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    request.setAttribute("exception", "토큰이 존재하지 않습니다");
                }
            }
        } catch (ClientException e) {
            logger.error("Could not set user authentication in security context", e);
            SecurityContextHolder.clearContext();
            request.setAttribute("exception", e.getMessage());
        } catch (Exception e) {
            logger.error("Authentication error: ", e);
            SecurityContextHolder.clearContext();
            request.setAttribute("exception", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 화이트리스트 경로는 필터 건너뛰기
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

}
