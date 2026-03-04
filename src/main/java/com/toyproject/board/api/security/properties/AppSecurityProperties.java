package com.toyproject.board.api.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security.url")
public class AppSecurityProperties {
    private List<String> whitelist = new ArrayList<>();
    private List<String> postWhitelist = new ArrayList<>();
    private List<String> getWhitelist = new ArrayList<>();

    public List<String> getAllExcludes() {
        List<String> all = new ArrayList<>(whitelist);
        all.addAll(postWhitelist);
        all.addAll(getWhitelist);
        return all;
    }
}
