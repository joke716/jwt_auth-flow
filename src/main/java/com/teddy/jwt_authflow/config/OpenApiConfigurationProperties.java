package com.teddy.jwt_authflow.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "com.teddy.jwtauthflow")
public class OpenApiConfigurationProperties {
    private OpenAPI openApi = new OpenAPI();

    @Getter
    @Setter
    public class OpenAPI {

        /**
         * Determines whether Swagger v3 API documentation and related endpoints are
         * accessible bypassing Authentication and Authorization checks. Swagger
         * endpoints are restricted by default.
         *
         * Can be used in profile-specific configuration files to control
         * access based on current environments.
         */
        private boolean enabled;

        private String title;
        private String description;
        private String apiVersion;

    }
}
