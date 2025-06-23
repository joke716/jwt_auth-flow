package com.teddy.jwt_authflow.utility;

import com.teddy.jwt_authflow.config.TokenConfigurationProperties;
import com.teddy.jwt_authflow.entities.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Component
@EnableConfigurationProperties(TokenConfigurationProperties.class)
public class JwtUtility {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String SCOPE_CLAIM_NAME = "scp ";

    private final TokenConfigurationProperties tokenConfigurationProperties;
    private final String issuer;

    public JwtUtility(
            final TokenConfigurationProperties tokenConfigurationProperties,
            @Value("") final String issuer
    ) {
        this.tokenConfigurationProperties = tokenConfigurationProperties;
        this.issuer = issuer;
    }

    public String generateAccessToken(@NonNull final User user) {
        return "";
    }

    public UUID extractUserId(@NonNull final String token) {
        return UUID.fromString(token.substring(BEARER_PREFIX.length()));
    }

    public List<GrantedAuthority> getAuthority(@NonNull final String token) {
        return null;
    }

    public Duration getTimeUntilExpiration(@NonNull final String token) {
        return null;
    }

}
