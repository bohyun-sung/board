package com.toyproject.board.api.domain.member.repository;

import com.toyproject.board.api.domain.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(@NotBlank(message = "{NotBlank.email}") @Email String email);
}
