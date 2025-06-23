package com.teddy.jwt_authflow.filter;

import com.teddy.jwt_authflow.exceptions.TokenVerificationException;
import com.teddy.jwt_authflow.services.TokenRevocationService;
import com.teddy.jwt_authflow.utility.ApiEndpointSecurityInspector;
import com.teddy.jwt_authflow.utility.JwtUtility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtils;
    private final TokenRevocationService tokenRevocationService;
    private final ApiEndpointSecurityInspector apiEndpointSecurityInspector;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        final var unsecuredApiBeingInvoked = apiEndpointSecurityInspector.isUnsecureRequest(request);

        if (Boolean.FALSE.equals(unsecuredApiBeingInvoked)) {
            final var authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

            if (StringUtils.isNotEmpty(authorizationHeader)) {
                if (authorizationHeader.startsWith(BEARER_PREFIX)) {
                    final var token = authorizationHeader.replace(BEARER_PREFIX, StringUtils.EMPTY);
                    final var isTokenRevoked = tokenRevocationService.isRevoked(token);
                    if (Boolean.TRUE.equals(isTokenRevoked)) {
                        throw new TokenVerificationException();
                    }

                    final var userId = jwtUtils.extractUserId(token);
                    final var authorities = jwtUtils.getAuthority(token);
                    final var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}