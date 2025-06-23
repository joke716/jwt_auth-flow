package com.teddy.jwt_authflow.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@Schema(title = "UserLoginRequest", accessMode = Schema.AccessMode.WRITE_ONLY)
public class UserLoginRequestDTO {

    @NotBlank(message = "emailId must not be empty")
    @Email(message = "emailId must be of valid format")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "emailId of user", example = "joke79@hanyang.ac.kr")
    private String emailId;


    @NotBlank(message = "password must not be empty")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "secure password to enable user login", example = "PasswOrd123!")
    private String password;

}
