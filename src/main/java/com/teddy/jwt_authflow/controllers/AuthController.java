package com.teddy.jwt_authflow.controllers;

import com.teddy.jwt_authflow.config.PublicEndpoint;
import com.teddy.jwt_authflow.dtos.TokenSuccessResponseDTO;
import com.teddy.jwt_authflow.dtos.UserLoginRequestDTO;
import com.teddy.jwt_authflow.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping(value = "/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PublicEndpoint
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Logs in user info the system", description = "Returns Access-token and Refresh-token on successfull authentication which provides access to protected endpoints")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successfull"),
//            @ApiResponse(responseCode = "401", description = "Bad credentials provided. Failed to authenticate user",
//                    content = @Content(schema = @Schema(implementation = ExceptionResponseDto.class))),
//            @ApiResponse(responseCode = "422", description = "Password has been compromised",
//                    content = @Content(schema = @Schema(implementation = ExceptionResponseDto.class)))
    })
    public ResponseEntity<TokenSuccessResponseDTO> loginUser(
            @Valid @RequestBody final UserLoginRequestDTO userLoginRequestDTO
    ) {
        final var tokenResponse = authenticationService.login(userLoginRequestDTO);
        return ResponseEntity.ok(tokenResponse);
    }
}
