package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.AuthResponse;
import com.obs.productmanagement.dto.LoginRequest;

public interface IAuthService {
    AuthResponse login(LoginRequest request);
}

