package com.teddy.jwt_authflow.config;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
//@ConfigurationProperties(prefix = "com.teddy.jwt_authflow.config.token")
public class TokenConfigurationProperties {



}
