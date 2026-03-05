package com.toyproject.board.api.security.oauth;

import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.domain.member.repository.MemberRepository;
import com.toyproject.board.api.dto.member.OAuth2Attributes;
import com.toyproject.board.api.dto.users.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        // 소셜 에서 준 사용자 정보 가져오기
        OAuth2User oAuth2User = super.loadUser(request);
        // 소셜 서비스 가져오기
        String registrationId = request.getClientRegistration()
                .getRegistrationId();

        String userNameAttributeName = request.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 소셜 속성 객체 생성
        OAuth2Attributes attributes = OAuth2Attributes.of(
                userNameAttributeName,
                oAuth2User.getAttributes());
        // 이메일로 회원 검색후 존재하면 업데이트, 없으면 신규 가입
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.modifyName(attributes.getName()))
                .orElseGet(() -> memberRepository.save(attributes.toEntity(registrationId)));

        return new UserPrincipal(member, attributes.getAttributes());
    }
}
