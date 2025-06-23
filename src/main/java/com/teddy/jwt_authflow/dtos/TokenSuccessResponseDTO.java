package com.teddy.jwt_authflow.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(value = PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@Schema(title = "TokenSuccessResponse", accessMode = Schema.AccessMode.READ_ONLY)
public class TokenSuccessResponseDTO {
    private String accessToken;
    private String refreshToken;
}
