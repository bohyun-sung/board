package com.toyproject.board.api.dto.member;

import com.toyproject.board.api.domain.member.entity.Member;
import com.toyproject.board.api.enums.ProviderType;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuth2Attributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;

    @Builder
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    /**
     * 구글 데이터 추출 로직
     */
    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {

        return OAuth2Attributes.builder()
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .build();
    }

    public Member toEntity(String registrationId) {
        ProviderType providerType = ProviderType.valueOf(registrationId.toUpperCase());
        return Member.of(email, name, providerType);
    }
}
