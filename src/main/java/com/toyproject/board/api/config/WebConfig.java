package com.toyproject.board.api.config;

import com.toyproject.board.api.resolver.CurrentUserIdxResolver;
import com.toyproject.board.api.resolver.CurrentUserRoleTypeResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUserIdxResolver currentUserIdxResolver;
    private final CurrentUserRoleTypeResolver currentUserRoleTypeResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserIdxResolver);
        resolvers.add(currentUserRoleTypeResolver);
    }
}
