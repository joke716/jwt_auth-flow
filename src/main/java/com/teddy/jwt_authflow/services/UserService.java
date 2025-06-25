package com.teddy.jwt_authflow.services;

import com.teddy.jwt_authflow.dtos.ResetPasswordRequestDTO;
import com.teddy.jwt_authflow.dtos.UserCreateRequestDTO;
import com.teddy.jwt_authflow.dtos.UserDetailDTO;
import com.teddy.jwt_authflow.dtos.UserUpdateRequestDTO;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;


public interface UserService {
    void create(@NonNull final UserCreateRequestDTO userCreateRequest);
    UserDetailDTO update(@NonNull final UUID userId, @NonNull UserUpdateRequestDTO userUpdateRequestDTO);
    List<UserDetailDTO> retrieve();
    UserDetailDTO getById(@NonNull final UUID userId);
    void resetPassword(@NonNull final ResetPasswordRequestDTO resetPasswordRequestDTO);
    void deactivate(@NonNull final UUID userId);
}
