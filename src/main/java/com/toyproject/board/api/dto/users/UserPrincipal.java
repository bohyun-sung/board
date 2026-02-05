package com.toyproject.board.api.dto.users;

import com.toyproject.board.api.domain.admin.entity.Admin;
import com.toyproject.board.api.enums.coverter.RoleType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class UserPrincipal implements UserDetails {
    private final Long idx;
    private final String userId;
    private final String passwoord;
    private final String email;
    private final RoleType roleType;
    Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Admin admin) {
        this.idx = admin.getIdx();
        this.userId = admin.getUserId();
        this.passwoord = admin.getPassword();
        this.email = admin.getEmail();
        this.authorities = admin.getAuthorities();
        this.roleType = admin.getRoleType();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwoord;
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
