package com.teddy.jwt_authflow.controllers;

import com.teddy.jwt_authflow.config.PublicEndpoint;
import com.teddy.jwt_authflow.dtos.TokenSuccessResponseDTO;
import com.teddy.jwt_authflow.dtos.UserLoginRequestDTO;
import com.teddy.jwt_authflow.dtos.ExceptionResponseDTO;
import com.teddy.jwt_authflow.exceptions.TokenVerificationException;
import com.teddy.jwt_authflow.services.AuthenticationService;
import com.teddy.jwt_authflow.utility.RefreshTokenHeaderProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenHeaderProvider refreshTokenHeaderProvider;

    @PublicEndpoint
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Logs in user info the system",
            description = "Returns Access-token and Refresh-token on successfull authentication which provides access to protected endpoints"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successfull"),
            @ApiResponse(responseCode = "401", description = "Bad credentials provided. Failed to authenticate user",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "Password has been compromised",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDTO.class)))
    })
    public ResponseEntity<TokenSuccessResponseDTO> loginUser(
            @Valid @RequestBody final UserLoginRequestDTO userLoginRequestDTO
    ) {
        final var tokenResponse = authenticationService.login(userLoginRequestDTO);
        return ResponseEntity.ok(tokenResponse);
    }

    @PublicEndpoint
    @PutMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Refreshes Access-Token for a user",
            description = "Provides a new Access-token against the user for which the non expired refresh-token is provided"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access-token refreshed"),
            @ApiResponse(responseCode = "403", description = "Refresh token has expired. Failed to refresh access token",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDTO.class)))
    })
    public ResponseEntity<TokenSuccessResponseDTO> refreshToken() {
        final var refreshToken = refreshTokenHeaderProvider.getRefreshToken().orElseThrow(TokenVerificationException::new);
        final var tokenResponse = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }

}
