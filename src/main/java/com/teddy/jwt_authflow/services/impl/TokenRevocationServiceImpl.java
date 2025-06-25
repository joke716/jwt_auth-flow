package com.teddy.jwt_authflow.services.impl;

import com.teddy.jwt_authflow.services.TokenRevocationService;
import com.teddy.jwt_authflow.utility.CacheManager;
import com.teddy.jwt_authflow.utility.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenRevocationServiceImpl implements TokenRevocationService {

    private final JwtUtility jwtUtility;
    private final CacheManager cacheManager;
    private final HttpServletRequest httpServletRequest;

    @Override
    public void revoke() {
        final var authHeader = Optional.ofNullable(httpServletRequest
                .getHeader("Authorization"))
                .orElseThrow(IllegalStateException::new);

        final var jti = jwtUtility.getJti(authHeader);
        final var ttl = jwtUtility.getTimeUntilExpiration(authHeader);
        cacheManager.save(jti, ttl);
    }


    @Override
    public boolean isRevoked(@NonNull String authHeader) {
        final var jti = jwtUtility.getJti(authHeader);
        return cacheManager.isPresent(jti);
    }

}
