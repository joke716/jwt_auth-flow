package com.teddy.jwt_authflow.controllers;

import com.teddy.jwt_authflow.dtos.UserCreateRequestDTO;
import com.teddy.jwt_authflow.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users")
@Tag(name = "User Management", description = "Endpoints for managing user profile details")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Creates a user account", description = "Registers a unique user record in the system corresponding to the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User account created successfully",
                content = @Content(schema = @Schema(implementation = Void.class))),
//            @ApiResponse(responseCode = "409", description = "User account with provided emailId already exists",
//                    content = @Content(schema = @Schema(implementation = Void.class))),

    })

    public ResponseEntity<HttpStatus> craeteUserAccount(
            @Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO
    ) {
        userService.createUser(userCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
