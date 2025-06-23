package com.teddy.jwt_authflow.utility;

import com.teddy.jwt_authflow.config.TokenConfigurationProperties;
import com.teddy.jwt_authflow.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@EnableConfigurationProperties(TokenConfigurationProperties.class)
public class JwtUtility {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String SCOPE_CLAIM_NAME = "scp";

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
        final var jti = String.valueOf(UUID.randomUUID());
        final var audience = String.valueOf(user.getId());
        final var accessTokenValidity = tokenConfigurationProperties.getAccessToken().getValidity();
        final var expiration = TimeUnit.MINUTES.toMillis(accessTokenValidity);
        final var currentTimestamp = new Date(System.currentTimeMillis());
        final var expirationTimestamp = new Date(System.currentTimeMillis() + expiration);
        final var scopes = user.getUserStatus().getScopes().stream().collect(Collectors.joining(StringUtils.SPACE));

        final var claims = new HashMap<String, String>();
        claims.put(SCOPE_CLAIM_NAME, scopes);

        return Jwts.builder()
                .claims(claims)
                .id(jti)
                .issuer(issuer)
                .issuedAt(currentTimestamp)
                .expiration(expirationTimestamp)
                .audience().add(audience)
                .and()
                .signWith(getPrivateKey(), Jwts.SIG.RS512)
                .compact();
    }

    public UUID extractUserId(@NonNull final String token) {
        final var audience = extractClaim(token, Claims::getAudience).iterator().next();
        return UUID.fromString(audience);
    }

    public String getJti(@NonNull final String token) {
        return extractClaim(token, Claims::getId);
    }

    public List<GrantedAuthority> getAuthority(@NonNull final String token) {
        final var scopes = extractClaim(token, claims -> claims.get(SCOPE_CLAIM_NAME, String.class));

        return Arrays.stream(scopes.split(StringUtils.SPACE))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Duration getTimeUntilExpiration(@NonNull final String token) {
        final var expirationTimeStamp = extractClaim(token, Claims::getExpiration).toInstant();
        final var currentTimestamp = new Date().toInstant();
        return Duration.between(currentTimestamp, expirationTimeStamp);
    }


    private <T> T extractClaim(@NonNull final String token, @NonNull final Function<Claims, T> claimsResolver) {
        final var sanitizedToken = token.replace(BEARER_PREFIX, StringUtils.EMPTY);
        final var claims = Jwts.parser()
                .requireIssuer(issuer)
                .verifyWith(getPublicKey())
                .build()
                .parseSignedClaims(sanitizedToken)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    @SneakyThrows
    private PrivateKey getPrivateKey() {
        final var privateKey = tokenConfigurationProperties.getAccessToken().getPrivateKey();
        final var sanitizedPrivatekey = sanitizeKey(privateKey);

        final var decodedPrivateKey = Decoders.BASE64.decode(sanitizedPrivatekey);
        final var spec = new PKCS8EncodedKeySpec(decodedPrivateKey);

        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    @SneakyThrows
    private PublicKey getPublicKey() {
        final var publicKey = tokenConfigurationProperties.getAccessToken().getPublicKey();
        final var sanitizedPublicKey = sanitizeKey(publicKey);

        final var decodedPublicKey = Decoders.BASE64.decode(sanitizedPublicKey);
        final var spec = new X509EncodedKeySpec(decodedPublicKey);

        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }


    private String sanitizeKey(@NonNull final String key) {
        return key
                .replace("-----BEGIN PUBLIC KEY-----", StringUtils.EMPTY)
                .replace("-----END PUBLIC KEY-----", StringUtils.EMPTY)
                .replace("-----BEGIN PRIVATE KEY-----", StringUtils.EMPTY)
                .replace("-----END PRIVATE KEY-----", StringUtils.EMPTY)
                .replaceAll("\\n", StringUtils.EMPTY)
                .replaceAll("\\s", StringUtils.EMPTY);
    }


}
