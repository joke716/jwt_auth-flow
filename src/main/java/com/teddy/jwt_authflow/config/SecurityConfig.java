package com.teddy.jwt_authflow.config;

import com.teddy.jwt_authflow.filter.JwtAuthenticationFilter;
import com.teddy.jwt_authflow.utility.ApiEndpointSecurityInspector;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final ApiEndpointSecurityInspector apiEndpointSecurityInspector;


    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .csrf(csrfConfigurer -> csrfConfigurer.disable())
                .exceptionHandling(exceptionConfigurer ->
                        exceptionConfigurer.authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .sessionManagement(sessionConfigurer ->
                        sessionConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authManager -> {
                    authManager
                            .requestMatchers(HttpMethod.GET, apiEndpointSecurityInspector.getPublicGetEndpoints().toArray(String[]::new)).permitAll()
                            .requestMatchers(HttpMethod.POST, apiEndpointSecurityInspector.getPublicPostEndpoints().toArray(String[]::new)).permitAll()
                            .requestMatchers(HttpMethod.PUT, apiEndpointSecurityInspector.getPublicPutEndpoints().toArray(String[]::new)).permitAll()
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        final var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "X-Refresh-Token", "Origin", "Content-Type", "Accept"));

        final var corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return corsConfigurationSource;
    }

}
