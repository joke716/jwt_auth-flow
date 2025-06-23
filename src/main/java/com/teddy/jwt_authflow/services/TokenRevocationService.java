package com.teddy.jwt_authflow.services;

import com.teddy.jwt_authflow.utility.CacheManager;
import com.teddy.jwt_authflow.utility.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenRevocationService {

    private final JwtUtility jwtUtility;
    // 캐시 선언
    private final CacheManager cacheManager;
    private final HttpServletRequest httpServletRequest;

    public void revoke() {
        final var authHeader = Optional.ofNullable(httpServletRequest.getHeader("Authorization")).orElseThrow(IllegalStateException::new);
        final var jti = jwtUtility.getJti(authHeader);
        final var ttl = jwtUtility.getTimeUntilExpiration(authHeader);
        // 캐시에 저장
        cacheManager.save(jti, ttl);

    }

    public boolean isRevoked(@NonNull final String authHeader) {
        final var jti = jwtUtility.getJti(authHeader);
        // 캐시여부에 따른 리턴값
        return cacheManager.isPresent(jti);
    }
}
