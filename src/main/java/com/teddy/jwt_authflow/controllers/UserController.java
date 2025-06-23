package com.teddy.jwt_authflow.controllers;

import com.teddy.jwt_authflow.config.PublicEndpoint;
import com.teddy.jwt_authflow.dtos.UserCreateRequestDTO;
import com.teddy.jwt_authflow.dtos.ExceptionResponseDTO;
import com.teddy.jwt_authflow.dtos.UserDetailDTO;
import com.teddy.jwt_authflow.dtos.UserUpdateRequestDTO;
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

    @PublicEndpoint
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
    public ResponseEntity<HttpStatus> craeteUserAccount(
            @Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO
    ) {
        userService.create(userCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    @PreAuthorize("hasAnyAuthority('userprofile.read', 'fullaccess')")
    public ResponseEntity<UserDetailDTO> retrieveUser() {
        final var userId = authenticatedUserIdProvider.getUserId();
        final var userDetail = userService.getById(userId);
        return ResponseEntity.ok(userDetail);
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
    @PreAuthorize("hasAnyAuthority('userprofile.update', 'fullaccess')")
    public ResponseEntity<HttpStatus> updateUser(
            @Valid @RequestBody final UserUpdateRequestDTO userUpdateRequestDTO
    ) {
        final var userId = authenticatedUserIdProvider.getUserId();
        userService.update(userId, userUpdateRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
