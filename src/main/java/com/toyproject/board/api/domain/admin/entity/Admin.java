package com.toyproject.board.api.domain.admin.entity;

import com.toyproject.board.api.domain.base.DefaultTimeStampEntity;
import com.toyproject.board.api.enums.coverter.RoleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"}),
        @UniqueConstraint(columnNames = {"email"})
})
@Entity
public class Admin extends DefaultTimeStampEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_idx", columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "role_type", nullable = false)
    @Convert(converter = RoleType.Converter.class)
    private RoleType roleType;

    /**
     * 관리자 생성,수정 생성자
     * @param name      이름
     * @param userId    아이디
     * @param password 비밀번호
     * @param email     이메일
     * @param phone     전화번호
     */
    public Admin(String name, String userId, String password, String email, String phone, RoleType roleType) {
        this.name = name;
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.roleType = roleType;
    }

    public static Admin of(String name, String userId, String password, String email, String phone, RoleType roleType) {
        return new Admin(name, userId, password, email, phone, roleType);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.roleType == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority(this.roleType.getKey()));
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