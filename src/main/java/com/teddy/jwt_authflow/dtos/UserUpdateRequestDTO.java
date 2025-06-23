package com.teddy.jwt_authflow.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategies.UpperCamelCaseStrategy.class)
@Schema(title = "UserUpdateRequest", accessMode = Schema.AccessMode.WRITE_ONLY)
public class UserUpdateRequestDTO {
    @NotBlank(message = "first-name must not be empty")
    @Schema(requiredMode = RequiredMode.REQUIRED, description = "first-name of user", example = "Teddy")
    private String firstName;

    @Schema(requiredMode = RequiredMode.NOT_REQUIRED, description = "last-name of user", example = "Kwak")
    private String lastName;
}
