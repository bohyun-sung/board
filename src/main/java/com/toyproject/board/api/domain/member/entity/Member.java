package com.toyproject.board.api.domain.member.entity;

import com.toyproject.board.api.domain.base.DefaultTimeStampEntity;
import com.toyproject.board.api.enums.RoleType;
import jakarta.persistence.*;
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
    private String provider;

    public Member(String email, String nickname, RoleType roleType, String provider) {
        this.email = email;
        this.nickname = nickname;
        this.roleType = roleType;
        this.provider = provider;
    }

    public Member modifyName(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.roleType == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority(this.roleType.getKey()));
    }
}
