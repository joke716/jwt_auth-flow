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

    public void create(@NonNull final UserCreateRequestDTO userCreationRequest) {
        final var emailId = userCreationRequest.getEmailId();
        final var userAccountExistsWithEmailId = userRepository.existsByEmailId(emailId);
        if (Boolean.TRUE.equals(userAccountExistsWithEmailId)) {
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


}
