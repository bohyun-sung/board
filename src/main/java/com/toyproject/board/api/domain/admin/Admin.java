package com.toyproject.board.api.domain.admin;

import com.toyproject.board.api.domain.base.DefaultTimeStampEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin")
@Entity
public class Admin extends DefaultTimeStampEntity {

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
}