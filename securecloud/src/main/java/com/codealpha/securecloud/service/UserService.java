package com.codealpha.securecloud.service;

import com.codealpha.securecloud.model.User;
import com.codealpha.securecloud.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EncryptionService encryptionService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.encryptionService = encryptionService;
    }

    public User createUser(
            String username,
            String password,
            String sensitiveData) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username is required"
            );
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must contain at least 6 characters"
            );
        }

        if (sensitiveData == null || sensitiveData.isBlank()) {
            throw new IllegalArgumentException(
                    "Sensitive data is required"
            );
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        String hashedPassword =
                passwordEncoder.encode(password);

        String encryptedData =
                encryptionService.encrypt(sensitiveData);

        User user = new User(
                username,
                hashedPassword,
                encryptedData
        );

        return userRepository.save(user);
    }

    public User getUser(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );
    }
}