package com.toyproject.board.api.security;

import com.toyproject.board.api.config.exception.ClientException;
import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.admin.repository.AdminRepository;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.member.repository.MemberRepository;
import com.toyproject.board.api.dto.users.UserPrincipal;
import com.toyproject.board.api.enums.ExceptionType;
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
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            return new UserPrincipal(admin.get());
        }
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ClientException(ExceptionType.NOT_FOUND, "해당 이메일을 찾을 수 없습니다"));
        return new UserPrincipal(member);
    }
}
