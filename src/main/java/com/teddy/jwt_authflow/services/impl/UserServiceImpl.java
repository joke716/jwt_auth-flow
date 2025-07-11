package com.teddy.jwt_authflow.services.impl;

import com.teddy.jwt_authflow.dtos.ResetPasswordRequestDTO;
import com.teddy.jwt_authflow.dtos.UserCreateRequestDTO;
import com.teddy.jwt_authflow.dtos.UserDetailDTO;
import com.teddy.jwt_authflow.dtos.UserUpdateRequestDTO;
import com.teddy.jwt_authflow.entities.Role;
import com.teddy.jwt_authflow.entities.User;
import com.teddy.jwt_authflow.entities.UserStatus;
import com.teddy.jwt_authflow.exceptions.AccountAlreadyExistsException;
import com.teddy.jwt_authflow.exceptions.InvalidCredentialsException;
import com.teddy.jwt_authflow.repositories.UserRepository;
import com.teddy.jwt_authflow.services.TokenRevocationService;
import com.teddy.jwt_authflow.services.UserService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRevocationService tokenRevocationService;


    @Override
    public void create(@NonNull UserCreateRequestDTO userCreateRequest) {
        final var emailId = userCreateRequest.getEmailId();
        final var userAccountExistsWithEmailId = userRepository.existsByEmailId(emailId);

        if (Boolean.TRUE.equals(userAccountExistsWithEmailId)) {
            System.out.println("userAccountExistsWithEmailId = " + userAccountExistsWithEmailId);
            throw new AccountAlreadyExistsException("Account with provided email-id already exists");
        }

        final var plainTextPassword = userCreateRequest.getPassword();

        if (userCreateRequest.getRoles() == null || userCreateRequest.getRoles().isEmpty()) {
            userCreateRequest.setRoles(Set.of(Role.USER));
        }

        final var user = new User();
        final var encodedPassword = passwordEncoder.encode(plainTextPassword);
        user.setFirstName(userCreateRequest.getFirstName());
        user.setLastName(userCreateRequest.getLastName());
        user.setEmailId(userCreateRequest.getEmailId());
        user.setRoles(userCreateRequest.getRoles()); // roles 설정
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

//    @CachePut(value = "userById", key = "#userId")
//    @Override
//    public void update(@NonNull UUID userId, @NonNull UserUpdateRequestDTO userUpdateRequestDTO) {
//        final var user = getUserById(userId);
//        user.setFirstName(userUpdateRequestDTO.getFirstName());
//        user.setLastName(userUpdateRequestDTO.getLastName());
//        userRepository.save(user);
//    }
    @CachePut(value = "userById", key = "#userId")
    @Override
    public UserDetailDTO update(@NonNull UUID userId, @NonNull UserUpdateRequestDTO userUpdateRequestDTO) {
        final var user = getUserById(userId);
        user.setFirstName(userUpdateRequestDTO.getFirstName());
        user.setLastName(userUpdateRequestDTO.getLastName());
        userRepository.save(user);

        return UserDetailDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .status(user.getUserStatus().getValue())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Cacheable(value = "users")
    @Override
    public List<UserDetailDTO> retrieve() {
        System.out.println("📦 DB에서 사용자 목록 조회 (캐시가 없을 경우만 호출)");
        final var users = userRepository.findAll();
        return users
                .stream()
                .map(user -> UserDetailDTO.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .emailId(user.getEmailId())
                        .status(user.getUserStatus().getValue())
                        .roles(user.getRoles()
                                .stream()
                                .map(Enum::name)
                                .collect(Collectors.toSet())
                        )
                        .createdAt(user.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Cacheable(value = "userById", key = "#userId")
    @Override
    public UserDetailDTO getById(@NonNull UUID userId) {
        final var user = getUserById(userId);
        return UserDetailDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .status(user.getUserStatus().getValue())
                .roles(user.getRoles()
                        .stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet())
                )
                .createdAt(user.getCreatedAt())
                .build();
    }

//    @CacheEvict(value = "userById", key = "#user.id")
    @Override
    public void resetPassword(@NonNull ResetPasswordRequestDTO resetPasswordRequestDTO) {
        final var user = userRepository.findByEmailId(resetPasswordRequestDTO.getEmailId())
                .orElseThrow(() -> new InvalidCredentialsException("No user exists with given email/current-password combination."));

        final var userId = user.getId();

        final var existingEncodedPassword = user.getPassword();
        final var plainTextCurrentPassword = resetPasswordRequestDTO.getCurrentPassword();
        final var isCorrectPassword = passwordEncoder.matches(plainTextCurrentPassword, existingEncodedPassword);
        if (Boolean.FALSE.equals(isCorrectPassword)) {
            throw new InvalidCredentialsException("No user exists with given email/current-password combination.");
        }

        final var newPassword = resetPasswordRequestDTO.getNewPassword();
        final var encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    @CacheEvict(cacheNames = {"userById", "users"}, key = "#userId", allEntries = true)
    @Override
    public void deactivate(@NonNull UUID userId) {
        final var user = getUserById(userId);
        user.setUserStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);
        tokenRevocationService.revoke();
    }


    private User getUserById(@NonNull final UUID userId) {
        return userRepository.findById(userId).orElseThrow(IllegalStateException::new);
    }
}
