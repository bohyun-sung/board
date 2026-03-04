package com.toyproject.board.api.security;

import com.toyproject.board.api.config.exception.ClientException;
import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.AdminRepository;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.member.repository.MemberRepository;
import com.toyproject.board.api.dto.users.UserPrincipal;
import com.toyproject.board.api.enums.ExceptionType;
import com.toyproject.board.api.enums.RoleType;
import com.toyproject.board.api.jwt.JwtUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        throw new UnsupportedOperationException("이메일은 기반은 지원하지 않습니다");
    }


    public UserDetails loadUserByUserInfo(JwtUserInfo userInfo) throws UsernameNotFoundException {
        if (userInfo.getRoleType() == RoleType.ADMIN) {
            Admin admin = adminRepository.findById(userInfo.getUserIdx())
                    .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND, "존재 하지 않는 관리자입니다"));
            return new UserPrincipal(admin);
        }
        Member member = memberRepository.findById(userInfo.getUserIdx())
                .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND, "해당 이메일을 찾을 수 없습니다"));
        return new UserPrincipal(member);

    }
}
