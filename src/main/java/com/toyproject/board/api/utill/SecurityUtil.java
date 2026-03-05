package com.toyproject.board.api.utill;

import com.toyproject.board.api.config.exception.ClientException;
import com.toyproject.board.api.dto.users.UserPrincipal;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    /**
     * [로그인용] 로그인 안되었으면 예외 발생
     * @return 인가처리된 계정 idx 반환
     */
    public static Long getCurrentMemberIdx() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() instanceof String) {
            throw new ClientException(ExceptionType.UNAUTHORIZED);
        }

        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        return userPrincipal.getIdx();
    }

    /**
     * [비로그인 허용] 로그인 안되어있으면 null 반환
     * @return 비로그인 null 로그인 인가 처리된 계정 idx 반환
     */
    public static Long getCurrentMemberIdxOptional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() instanceof String) {
            return null;
        }
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        return userPrincipal.getIdx();
    }

    /**
     * @return 인가 처리된 계정 RoleType 반환
     */
    public static RoleType getCurrentRoleType() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal.getRoleType();
    }
}
