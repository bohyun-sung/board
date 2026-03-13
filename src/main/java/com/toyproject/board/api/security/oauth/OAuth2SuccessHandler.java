package com.toyproject.board.api.security.oauth;

import com.toyproject.board.api.jwt.properties.JwtTokenProperty;
import com.toyproject.board.api.constants.AuthConstants;
import com.toyproject.board.api.dto.users.UserPrincipal;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.jwt.RefreshToken;
import com.toyproject.board.api.jwt.properties.AppJwtProperties;
import com.toyproject.board.api.jwt.repository.RefreshTokenRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Value("${app.callback.url}")
    private String oathCallbackUrl;
    private final JwtTokenProperty jwtTokenProperty;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppJwtProperties appJwtProperties;


    @PostConstruct
    public void init() {
        log.info("현재 로드된 Callback URL: {}", oathCallbackUrl);
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long memberIdx = userPrincipal.getIdx();
        RoleType roleType = userPrincipal.getRoleType();

        String accessToken = jwtTokenProperty.createToken(memberIdx, roleType);
        String refreshToken = jwtTokenProperty.createRefreshToken(memberIdx, roleType);

        refreshTokenRepository.save(RefreshToken.of(memberIdx, RoleType.USER, refreshToken));

        String targetUrl = UriComponentsBuilder.fromUriString(oathCallbackUrl)
                .queryParam("token", accessToken)
                .build().toUriString();

        // RefreshToken 쿠키에 추가
        ResponseCookie cookie = ResponseCookie.from(AuthConstants.REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(appJwtProperties.getRefreshTTL())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
