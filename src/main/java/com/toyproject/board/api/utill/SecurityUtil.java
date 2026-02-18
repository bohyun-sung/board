package com.toyproject.board.api.utill;

import com.toyproject.board.api.config.exception.ClientException;
import com.toyproject.board.api.dto.users.UserPrincipal;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.coverter.RoleType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentMemberIdx() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() instanceof String) {
            throw new ClientException(ExceptionType.BAD_REQUEST, "잘못된 유저정보");
        }

        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        return userPrincipal.getIdx();
    }

    public static RoleType getCurrentRoleType() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal.getRoleType();
    }
}
