package com.codealpha.securecloud.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKeySpec secretKey;

    public EncryptionService(
            @Value("${securecloud.encryption.key}") String key) {

        byte[] keyBytes = Base64.getDecoder().decode(key);

        if (keyBytes.length != 32) {
            throw new IllegalArgumentException(
                    "Encryption key must decode to exactly 32 bytes"
            );
        }

        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String plainText) {

        try {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            GCMParameterSpec spec =
                    new GCMParameterSpec(TAG_LENGTH, iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            byte[] encrypted =
                    cipher.doFinal(
                            plainText.getBytes(StandardCharsets.UTF_8)
                    );

            byte[] result =
                    new byte[IV_LENGTH + encrypted.length];

            System.arraycopy(
                    iv,
                    0,
                    result,
                    0,
                    IV_LENGTH
            );

            System.arraycopy(
                    encrypted,
                    0,
                    result,
                    IV_LENGTH,
                    encrypted.length
            );

            return Base64.getEncoder().encodeToString(result);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to encrypt sensitive data",
                    e
            );
        }
    }

    public String decrypt(String encryptedText) {

        try {
            byte[] combined =
                    Base64.getDecoder().decode(encryptedText);

            byte[] iv =
                    new byte[IV_LENGTH];

            byte[] encrypted =
                    new byte[combined.length - IV_LENGTH];

            System.arraycopy(
                    combined,
                    0,
                    iv,
                    0,
                    IV_LENGTH
            );

            System.arraycopy(
                    combined,
                    IV_LENGTH,
                    encrypted,
                    0,
                    encrypted.length
            );

            Cipher cipher =
                    Cipher.getInstance(ALGORITHM);

            GCMParameterSpec spec =
                    new GCMParameterSpec(TAG_LENGTH, iv);

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    spec
            );

            byte[] decrypted =
                    cipher.doFinal(encrypted);

            return new String(
                    decrypted,
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to decrypt sensitive data",
                    e
            );
        }
    }
}