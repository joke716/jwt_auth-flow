package com.teddy.jwt_authflow.controllers;

import com.teddy.jwt_authflow.config.PublicEndpoint;
import com.teddy.jwt_authflow.dtos.*;
import com.teddy.jwt_authflow.exceptions.TokenVerificationException;
import com.teddy.jwt_authflow.services.AuthenticationService;
import com.teddy.jwt_authflow.services.UserService;
import com.teddy.jwt_authflow.utility.RefreshTokenHeaderProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and token management")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final RefreshTokenHeaderProvider refreshTokenHeaderProvider;

    @PublicEndpoint
    @PostMapping("/signup")
    @Operation(
            summary = "Creates a user account",
            description = "Registers a unique user record in the system corresponding to the provided information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User account created successfully",
                    content = @Content(schema = @Schema(implementation = void.class))),
            @ApiResponse(responseCode = "409", description = "User account with provided email-id already exists",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "Provided password is compromised",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<HttpStatus>> craeteUserAccount(
            @Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO
    ) {
        userService.create(userCreateRequestDTO);
        ApiResponseDTO<HttpStatus> response = ApiResponseDTO.<HttpStatus>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("User account created successfully")
                .data(HttpStatus.CREATED)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

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
    public ResponseEntity<ApiResponseDTO<TokenSuccessResponseDTO>> loginUser(
            @Valid @RequestBody final UserLoginRequestDTO userLoginRequestDTO
    ) {
        final var tokenResponse = authenticationService.login(userLoginRequestDTO);
        ApiResponseDTO<TokenSuccessResponseDTO> response = ApiResponseDTO.<TokenSuccessResponseDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User account get loade successfully")
                .data(tokenResponse)
                .build();
        return ResponseEntity.ok(response);
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
