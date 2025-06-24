package com.teddy.jwt_authflow.controllers;

import com.teddy.jwt_authflow.config.PublicEndpoint;
import com.teddy.jwt_authflow.dtos.*;
import com.teddy.jwt_authflow.services.UserService;
import com.teddy.jwt_authflow.utility.AuthenticatedUserIdProvider;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
@Tag(name = "User Management", description = "Endpoints for managing user profile details")
public class UserController {

    private final UserService userService;
    private final AuthenticatedUserIdProvider authenticatedUserIdProvider;


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(
            summary = "Creates a user account",
            description = "Registers a unique user record in the system corresponding to the provided information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User account created successfully",
                    content = @Content(schema = @Schema(implementation = Void.class))),
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


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Retrieves current logged-in user's account details",
            description = "Private endpoint which retreives user account details against the Access-token JWT provided in headers"
    )
    @ApiResponse(
            responseCode = "200",
            description = "User account details retrieved successfully"
    )
//    @PreAuthorize("hasAnyAuthority('userprofile.read', 'fullaccess')")
    public ResponseEntity<ApiResponseDTO<UserDetailDTO>> retrieveUser() {
        final var userId = authenticatedUserIdProvider.getUserId();
        final var userDetail = userService.getById(userId);
        ApiResponseDTO<UserDetailDTO> response = ApiResponseDTO.<UserDetailDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User account details retrieved successfully")
                .data(userDetail)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Updates user profile details",
            description = "Updates profile details corresponding to logged-in user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "User account details updated successfully",
            content = @Content(schema = @Schema(implementation = Void.class))
    )
//    @PreAuthorize("hasAnyAuthority('userprofile.update', 'fullaccess')")
    public ResponseEntity<ApiResponseDTO<HttpStatus>> updateUser(
            @Valid @RequestBody final UserUpdateRequestDTO userUpdateRequestDTO
    ) {
        final var userId = authenticatedUserIdProvider.getUserId();
        userService.update(userId, userUpdateRequestDTO);
        ApiResponseDTO<HttpStatus> response = ApiResponseDTO.<HttpStatus>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User account details updated successfully")
                .data(HttpStatus.OK)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PublicEndpoint
    @PutMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Resets user's current password",
            description = "Non secured endpoint to help user reset their current password with a new password of choosing."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(schema = @Schema(implementation = void.class))),
            @ApiResponse(responseCode = "401", description = "No user account exists with given email/current-password combination.",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDTO.class))),
            @ApiResponse(responseCode = "422", description = "Provided new password is compromised",
                    content = @Content(schema = @Schema(implementation = ExceptionResponseDTO.class)))
    })
    public ResponseEntity<ApiResponseDTO<HttpStatus>> resetPassword(
            @Valid @RequestBody final ResetPasswordRequestDTO resetPasswordRequestDTO
    ) {
        userService.resetPassword(resetPasswordRequestDTO);
        ApiResponseDTO<HttpStatus> response = ApiResponseDTO.<HttpStatus>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Resets user's current password")
                .data(HttpStatus.OK)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(value = "/deactivate")
    @Operation(
            summary = "Deactivates current logged-in user's profile",
            description = "Deactivates user's profile: can only be undone by praying to a higher power or contacting our vanished customer support."
    )
    @ApiResponse(
            responseCode = "204",
            description = "User profile successfully deactivated",
            content = @Content(schema = @Schema(implementation = void.class))
    )
    @PreAuthorize("hasAnyAuthority('userprofile.update', 'fullaccess')")
    public ResponseEntity<ApiResponseDTO<HttpStatus>> deactivateUser() {
        final var userId = authenticatedUserIdProvider.getUserId();
        userService.deactivate(userId);
        ApiResponseDTO<HttpStatus> response = ApiResponseDTO.<HttpStatus>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User profile successfully deactivated")
                .data(HttpStatus.NO_CONTENT)
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);

    }
}
