package com.teddy.jwt_authflow.services;


import com.teddy.jwt_authflow.dtos.UserCreateRequestDTO;
import com.teddy.jwt_authflow.dtos.UserDetailDTO;
import com.teddy.jwt_authflow.dtos.UserUpdateRequestDTO;
import com.teddy.jwt_authflow.entities.User;
import com.teddy.jwt_authflow.exceptions.AccountAlreadyExistsException;
import com.teddy.jwt_authflow.repositories.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void create(@NonNull final UserCreateRequestDTO userCreationRequest) {
        final var emailId = userCreationRequest.getEmailId();
        final var userAccountExistsWithEmailId = userRepository.existsByEmailId(emailId);

        if (Boolean.TRUE.equals(userAccountExistsWithEmailId)) {
            System.out.println("userAccountExistsWithEmailId = " + userAccountExistsWithEmailId);
            throw new AccountAlreadyExistsException("Account with provided email-id already exists");
        }

        final var plainTextPassword = userCreationRequest.getPassword();
//        final var isPasswordCompromised = compromisedPasswordChecker.check(plainTextPassword).isCompromised();
//        if (Boolean.TRUE.equals(isPasswordCompromised)) {
//            throw new CompromisedPasswordException("The provided password is compromised and cannot be used for account creation.");
//        }

        final var user = new User();
        final var encodedPassword = passwordEncoder.encode(plainTextPassword);
        user.setFirstName(userCreationRequest.getFirstName());
        user.setLastName(userCreationRequest.getLastName());
        user.setEmailId(userCreationRequest.getEmailId());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    public void update(@NonNull final UUID userId, @NonNull UserUpdateRequestDTO userUpdateRequestDTO) {
        final var user = getUserById(userId);
        user.setFirstName(userUpdateRequestDTO.getFirstName());
        user.setLastName(userUpdateRequestDTO.getLastName());
        userRepository.save(user);
    }

    public UserDetailDTO getById(@NonNull final UUID userId) {
        final var user = getUserById(userId);
        return UserDetailDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .status(user.getUserStatus().getValue())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private User getUserById(@NonNull final UUID userId) {
        return userRepository.findById(userId).orElseThrow(IllegalStateException::new);
    }

}
