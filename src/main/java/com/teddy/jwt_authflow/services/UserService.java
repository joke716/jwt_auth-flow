package com.teddy.jwt_authflow.services;


import com.teddy.jwt_authflow.dtos.ResetPasswordRequestDTO;
import com.teddy.jwt_authflow.dtos.UserCreateRequestDTO;
import com.teddy.jwt_authflow.dtos.UserDetailDTO;
import com.teddy.jwt_authflow.dtos.UserUpdateRequestDTO;
import com.teddy.jwt_authflow.entities.User;
import com.teddy.jwt_authflow.entities.UserStatus;
import com.teddy.jwt_authflow.exceptions.AccountAlreadyExistsException;
import com.teddy.jwt_authflow.exceptions.InvalidCredentialsException;
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
    private final TokenRevocationService tokenRevocationService;

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

    public void resetPassword(@NonNull final ResetPasswordRequestDTO resetPasswordRequestDTO) {
        final var user = userRepository.findByEmailId(resetPasswordRequestDTO.getEmailId())
                .orElseThrow(() -> new InvalidCredentialsException("No user exists with given email/current-password combination."));

        final var existingEncodedPassword = user.getPassword();
        final var plainTextCurrentPassword = resetPasswordRequestDTO.getCurrentPassword();
        final var isCorrectPassword = passwordEncoder.matches(plainTextCurrentPassword, existingEncodedPassword);
        if (Boolean.FALSE.equals(isCorrectPassword)) {
            throw new InvalidCredentialsException("No user exists with given email/current-password combination.");
        }

        final var newPassword = resetPasswordRequestDTO.getNewPassword();
//        final var isNewPasswordCompromised = compromisedPasswordChecker.check(newPassword).isCompromised();
//        if (Boolean.TRUE.equals(isNewPasswordCompromised)) {
//            throw new CompromisedPasswordException("New password selected is compromised and cannot be used.");
//        }

        final var encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    public void deactivate(@NonNull final UUID userId) {
        final var user = getUserById(userId);
        user.setUserStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);

        tokenRevocationService.revoke();
    }

    private User getUserById(@NonNull final UUID userId) {
        return userRepository.findById(userId).orElseThrow(IllegalStateException::new);
    }

}
