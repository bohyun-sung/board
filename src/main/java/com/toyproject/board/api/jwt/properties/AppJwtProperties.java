package com.toyproject.board.api.jwt.properties;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.Key;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class AppJwtProperties {
    private String secret;
    private Long accessTTL;
    private Long refreshTTL;

    public Key getSecretAsObject() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
