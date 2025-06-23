package com.teddy.jwt_authflow.utility;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuthenticatedUserIdProvider {
    public UUID getUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(UUID.class::isInstance)
                .map(UUID.class::cast)
                .orElseThrow(IllegalStateException::new);
    }
}
