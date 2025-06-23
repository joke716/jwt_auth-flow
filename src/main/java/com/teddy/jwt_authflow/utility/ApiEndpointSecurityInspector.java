package com.teddy.jwt_authflow.utility;


import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.teddy.jwt_authflow.config.OpenApiConfigurationProperties;
import com.teddy.jwt_authflow.config.PublicEndpoint;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import io.swagger.v3.oas.models.PathItem.HttpMethod;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({ OpenApiConfigurationProperties.class, WebEndpointProperties.class})
public class ApiEndpointSecurityInspector {

    private final WebEndpointProperties webEndpointProperties;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final OpenApiConfigurationProperties openApiConfigurationProperties;

    private static final List<String> SWAGGER_V3_PATHS = List.of("/swagger-ui**/**", "/v3/api-docs**/**");

    @Getter
    private List<String> publicGetEndpoints = new ArrayList<String>();
    @Getter
    private List<String> publicPostEndpoints = new ArrayList<String>();
    @Getter
    private List<String> publicPutEndpoints = new ArrayList<String>();

    @PostConstruct
    public void init() {
        final var handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        handlerMethods.forEach((requestInfo, handlerMethod) -> {
            if (handlerMethod.hasMethodAnnotation(PublicEndpoint.class)) {
                final var httpMethod = requestInfo.getMethodsCondition().getMethods().iterator().next().asHttpMethod();
                final var apiPaths = requestInfo.getPathPatternsCondition().getPatternValues();

                if (httpMethod.equals(GET)) {
                    publicGetEndpoints.addAll(apiPaths);
                } else if (httpMethod.equals(POST)) {
                    publicPostEndpoints.addAll(apiPaths);
                } else if (httpMethod.equals(PUT)) {
                    publicPutEndpoints.addAll(apiPaths);
                }
            }
        });

        final var openApiEnable = openApiConfigurationProperties.getOpenApi().isEnabled();
        if (Boolean.TRUE.equals(openApiEnable)) {
            publicGetEndpoints.addAll(SWAGGER_V3_PATHS);
        }

        final var actuatorEndpoints = getActuatorEndpoints();
        publicGetEndpoints.addAll(actuatorEndpoints);

    }


    public boolean isUnsecureRequest(@NonNull final HttpServletRequest request) {
        final var requestHttpMethod = HttpMethod.valueOf(request.getMethod());
        var unsecuredApiPaths = getUnsecuredApiPaths(requestHttpMethod);
        unsecuredApiPaths = Optional.ofNullable(unsecuredApiPaths).orElseGet(ArrayList::new);

        return unsecuredApiPaths.stream().anyMatch(apiPath -> new AntPathMatcher().match(apiPath, request.getRequestURI()));
    }

    /**
     * Retrieves the list of unsecured API paths based on the provided HTTP method.
     *
     * @param httpMethod The HTTP method for which unsecured paths are to be retrieved.
     * @return A list of unsecured API paths for the specified HTTP method.s
     */
    private List<String> getUnsecuredApiPaths(@NonNull final HttpMethod httpMethod) {
        switch (httpMethod) {
            case GET:
                return publicGetEndpoints;
            case POST:
                return publicPostEndpoints;
            case PUT:
                return publicPutEndpoints;
            default:
                return Collections.emptyList();
        }
    }


    private List<String> getActuatorEndpoints() {
        final var basePath = webEndpointProperties.getBasePath();
        final var includedEndpoints = webEndpointProperties.getExposure().getInclude();
        final var excludedEndpoints = webEndpointProperties.getExposure().getExclude();

        return includedEndpoints.stream()
                .filter(Predicate.not(excludedEndpoints::contains))
                .flatMap(endpoint -> Stream.of(
                        String.format("%s/%s", basePath, endpoint),
                        String.format("%s/%s/*", basePath, endpoint)))
                .toList();
    }

}
