package com.teddy.jwt_authflow.services;

import com.teddy.jwt_authflow.dtos.TokenSuccessResponseDTO;
import com.teddy.jwt_authflow.dtos.UserLoginRequestDTO;
import lombok.NonNull;

public interface AuthenticationService {
    TokenSuccessResponseDTO login(@NonNull final UserLoginRequestDTO userLoginRequest);
    TokenSuccessResponseDTO refreshToken(@NonNull final String refreshToken);
}
