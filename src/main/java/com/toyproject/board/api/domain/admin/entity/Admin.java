package com.toyproject.board.api.domain.admin.entity;

import com.toyproject.board.api.domain.base.DefaultTimeStampEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin")
@Entity
public class Admin extends DefaultTimeStampEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_idx", columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "name", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '이름'")
    private String name;

    @Column(name = "user_id", columnDefinition = "VARCHAR(100) NOT NULL COMMENT '아이디'")
    private String userId;

    @Column(name = "password", columnDefinition = "VARCHAR(100) NOT NULL COMMENT '비밀번호'")
    private String password;

    @Column(name = "email", columnDefinition = "VARCHAR(100) NOT NULL COMMENT '이메일'")
    private String email;

    @Column(name = "phone", columnDefinition = "VARCHAR(20) NOT NULL COMMENT '핸드폰'")
    private String phone;


    @Column(name = "role", columnDefinition = "")
    private String role;

    /**
     * 관리자 생성,수정 생성자
     * @param name      이름
     * @param userId    아이디
     * @param password 비밀번호
     * @param email     이메일
     * @param phone     전화번호
     */
    public Admin(String name, String userId, String password, String email, String phone, String role) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public static Admin of(String name, String userId, String password, String email, String phone, String role) {
        return new Admin(name, userId, password, email, phone, role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public String getUsername() {
        return this.userId;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public void updatePassword(String modifyPassword) {
        this.password = modifyPassword;
    }
}