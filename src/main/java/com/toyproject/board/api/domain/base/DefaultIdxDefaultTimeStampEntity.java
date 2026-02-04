package com.toyproject.board.api.domain.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;

import org.apache.coyote.BadRequestException;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class DefaultIdxDefaultTimeStampEntity extends DefaultTimeStampEntity {

    @CreatedBy
    @Column(name = "rg_idx", columnDefinition = "BIGINT UNSIGNED COMMENT '등록한 Idx'")
    protected Long registerIdx;

    @Column(name = "rg_role", columnDefinition = "VARCHAR(10) COMMENT '등록한 권한'")
    protected String registerRole;

    @LastModifiedBy
    @Column(name = "up_idx", columnDefinition = "BIGINT UNSIGNED COMMENT '변경한 Idx'")
    protected Long updateIdx;

    @PrePersist
    public void prePersistRole() {
        this.registerRole = getCurrentUserRole();
    }

    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "ROLE_GEEST";
        }

        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알수없는"));
    }
}
