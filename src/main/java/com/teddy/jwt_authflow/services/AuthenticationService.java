package com.teddy.jwt_authflow.services;

import com.teddy.jwt_authflow.config.TokenConfigurationProperties;
import com.teddy.jwt_authflow.dtos.TokenSuccessResponseDTO;
import com.teddy.jwt_authflow.dtos.UserLoginRequestDTO;
import com.teddy.jwt_authflow.exceptions.InvalidCredentialsException;
import com.teddy.jwt_authflow.repositories.UserRepository;
import com.teddy.jwt_authflow.utility.CacheManager;
import com.teddy.jwt_authflow.utility.JwtUtility;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenConfigurationProperties.class)
public class AuthenticationService {

    private final JwtUtility jwtUtility;
    private final CacheManager cacheManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenConfigurationProperties tokenConfigurationProperties;

    public TokenSuccessResponseDTO login(@NonNull final UserLoginRequestDTO userLoginRequestDTO) {
        final var user = userRepository.findByEmailId(userLoginRequestDTO.getEmailId())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid login credentials provided.") );

        final var encodedPassword = user.getPassword();
        final var plainTextPassword = userLoginRequestDTO.getPassword();
        final var isCorrectPassword = passwordEncoder.matches(plainTextPassword, encodedPassword);
        if (Boolean.FALSE.equals(isCorrectPassword)) {
            throw new InvalidCredentialsException("Invalid login credentials provided.");
        }

        final var accessToken = jwtUtility.generateAccessToken(user);
        final var refreshToken = jwtUtility.generateAccessToken(user);

        final var refreshTokenValidity = tokenConfigurationProperties.getRefreshToken().getValidity();

        cacheManager.save(refreshToken, user.getId(), Duration.ofMinutes(refreshTokenValidity));

        return TokenSuccessResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

}
