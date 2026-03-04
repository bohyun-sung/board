package com.toyproject.board.api.dto.users;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.enums.RoleType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class UserPrincipal implements UserDetails, OAuth2User {
    private final Long idx;
    private final String userId;
    private final String password;
    private final String email;
    private final RoleType roleType;
    private Map<String, Object> attributes;
    Collection<? extends GrantedAuthority> authorities;

    /**
     * [Admin] 생성자
     * @param admin 관리자
     */
    public UserPrincipal(Admin admin) {
        this.idx = admin.getIdx();
        this.userId = admin.getUserId();
        this.password = admin.getPassword();
        this.email = admin.getEmail();
        this.authorities = admin.getAuthorities();
        this.roleType = admin.getRoleType();
    }

    /**
     * [Member] @CustomUserDetailsService 사용
     * @param member 사용자
     */
    public UserPrincipal(Member member) {
        this.idx = member.getIdx();
        this.userId = member.getEmail();
        this.password = member.getPassword();
        this.email = member.getEmail();
        this.roleType = member.getRoleType();
        this.authorities = member.getAuthorities();
    }

    /**
     * [Member]
     * @param member 사용자
     * @param attributes oauth에서 제공 받은 정보
     */
    public UserPrincipal(Member member, Map<String, Object> attributes) {
        this.idx = member.getIdx();
        this.userId = member.getEmail();
        this.password = member.getPassword();
        this.email = member.getEmail();
        this.roleType = member.getRoleType();
        this.authorities = member.getAuthorities();
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(idx);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
