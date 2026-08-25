package com.codealpha.securecloud.controller;

import com.codealpha.securecloud.dto.RegisterRequest;
import com.codealpha.securecloud.model.User;
import com.codealpha.securecloud.service.CapabilityService;
import com.codealpha.securecloud.service.EncryptionService;
import com.codealpha.securecloud.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")

public class UserController {

    private final UserService userService;
    private final CapabilityService capabilityService;
    private final EncryptionService encryptionService;

    public UserController(
            UserService userService,
            CapabilityService capabilityService,
            EncryptionService encryptionService) {

        this.userService = userService;
        this.capabilityService = capabilityService;
        this.encryptionService = encryptionService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterRequest request) {

        if (request.getUsername() == null ||
                request.getPassword() == null ||
                request.getSensitiveData() == null ||
                request.getCapabilityCode() == null) {

            return ResponseEntity
                    .badRequest()
                    .body("All fields are required");
        }

        if (!capabilityService.isValid(
                request.getCapabilityCode())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Invalid capability code");
        }

        try {

            User user = userService.createUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getSensitiveData()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            new RegisterResponse(
                                    user.getId(),
                                    user.getUsername(),
                                    "User registered successfully"
                            )
                    );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(
            @PathVariable Long id) {

        try {

            User user = userService.getUser(id);

            String decryptedData =
                    encryptionService.decrypt(
                            user.getSensitiveData()
                    );

            return ResponseEntity.ok(
                    new UserResponse(
                            user.getId(),
                            user.getUsername(),
                            decryptedData
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }

    public static class RegisterResponse {

        private final Long id;
        private final String username;
        private final String message;

        public RegisterResponse(
                Long id,
                String username,
                String message) {

            this.id = id;
            this.username = username;
            this.message = message;
        }

        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class UserResponse {

        private final Long id;
        private final String username;
        private final String sensitiveData;

        public UserResponse(
                Long id,
                String username,
                String sensitiveData) {

            this.id = id;
            this.username = username;
            this.sensitiveData = sensitiveData;
        }

        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getSensitiveData() {
            return sensitiveData;
        }
    }
}