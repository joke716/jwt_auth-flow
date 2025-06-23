package com.teddy.jwt_authflow.dtos;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategies.UpperCamelCaseStrategy.class)
@Schema(title = "ResetPasswordRequest", accessMode = Schema.AccessMode.WRITE_ONLY)
public class ResetPasswordRequestDTO {
    @NotBlank(message = "emailId must not be empty")
    @Email(message = "emailId must be of valid format")
    @Schema(requiredMode = RequiredMode.REQUIRED, description = "emailId of user", example = "joke79@hanyang.ac.kr")
    private String emailId;

    @NotBlank(message = "current-password must not be empty")
    @Schema(requiredMode = RequiredMode.REQUIRED, description = "current-password of the user", example = "PasswOrd123!")
    private String currentPassword;

    @NotBlank(message = "new-password must not be empty")
    @Schema(requiredMode = RequiredMode.REQUIRED, description = "new-password of the user")
    private String newPassword;
}
