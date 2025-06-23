package com.teddy.jwt_authflow.services;

import com.teddy.jwt_authflow.dtos.UserCreateRequestDTO;
import com.teddy.jwt_authflow.entities.User;
import com.teddy.jwt_authflow.exceptions.AccountAlreadyExistsException;
import com.teddy.jwt_authflow.repositories.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(@NonNull final UserCreateRequestDTO userCreateRequestDTO) {
        final var emailId = userCreateRequestDTO.getEmailId();
        final var userAccountExistsWithEmailId = userRepository.existsByEmailId(emailId);
        if (Boolean.TRUE.equals(userAccountExistsWithEmailId)) {
            // 에러 핸들링
            throw new AccountAlreadyExistsException("Account with provided emailId already exists");
        }

        final var plainPassword = userCreateRequestDTO.getPassword();

        final var user = new User();
        final var encodedPassword = passwordEncoder.encode(plainPassword);
        user.setFirstName(userCreateRequestDTO.getFirstName());
        user.setLastName(userCreateRequestDTO.getLastName());
        user.setEmailId(emailId);
        user.setPassword(encodedPassword);
        userRepository.save(user);

    }


}
