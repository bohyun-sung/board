package com.toyproject.board.api.domain.member.entity;

import com.toyproject.board.api.domain.base.DefaultTimeStampEntity;
import com.toyproject.board.api.enums.ProviderType;
import com.toyproject.board.api.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member extends DefaultTimeStampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_idx", columnDefinition = "BIGINT UNSIGNED")
    private Long idx;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "role_type", nullable = false)
    @Convert(converter = RoleType.Converter.class)
    private RoleType roleType;

    @Column(name = "provider", length = 20)
    @Convert(converter = ProviderType.Converter.class)
    private ProviderType provider;

    private Member(String email, String nickname, RoleType roleType, ProviderType providerType) {
        this.email = email;
        this.nickname = nickname;
        this.roleType = roleType;
        this.provider = providerType;
    }

    private Member(String email, String nickname, String phone, String password, RoleType roleType, ProviderType providerType) {
        this.email = email;
        this.nickname = nickname;
        this.phone = phone;
        this.password = password;
        this.roleType = roleType;
        this.provider = providerType;
    }

    public static Member of(String email, String nickname, ProviderType providerType) {
        return new Member(email, nickname, RoleType.USER, providerType);
    }

    public static Member of(String email, String nickname, String phone, String password, ProviderType providerType) {
        return new Member(email, nickname, phone, password, RoleType.USER, providerType);
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.roleType == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority(this.roleType.getKey()));
    }

    public Member modifyName(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public void updatePassword(String encodePassword) {
        this.password = encodePassword;
    }
}
