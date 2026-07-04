package com.cluehub.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "feilian")
public class FeilianProperties {
    private String baseUri;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String postLogoutRedirectUri;
}
