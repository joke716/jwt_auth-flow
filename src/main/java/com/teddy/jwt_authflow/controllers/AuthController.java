package com.teddy.jwt_authflow.controllers;

import com.teddy.jwt_authflow.dtos.TokenSuccessResponseDTO;
import com.teddy.jwt_authflow.dtos.UserLoginRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
public class AuthController {

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Logs in user info the system", description = "Returns Access-token and Refresh-token on successfull authentication which provides access to protected endpoints")
    public ResponseEntity<TokenSuccessResponseDTO> loginUser(
            @Valid @RequestBody final UserLoginRequestDTO userLoginRequestDTO
    ) {
        return ResponseEntity.ok(new TokenSuccessResponseDTO());
    }
}
