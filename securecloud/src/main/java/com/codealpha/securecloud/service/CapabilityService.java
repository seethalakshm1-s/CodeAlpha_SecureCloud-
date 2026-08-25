package com.codealpha.securecloud.service;

import org.springframework.stereotype.Service;

@Service
public class CapabilityService {

    private static final String CAPABILITY_CODE = "CAP001";

    public boolean isValid(String providedCode) {
        return CAPABILITY_CODE.equals(providedCode);
    }
}