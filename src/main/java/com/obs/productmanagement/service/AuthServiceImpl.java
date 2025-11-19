package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.AuthResponse;
import com.obs.productmanagement.dto.LoginRequest;
import com.obs.productmanagement.dto.UserResponse;
import com.obs.productmanagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserService userService;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest request) {
        // Vérifier les credentials via UserService
        UserResponse user = userService.getUserByNameOrEmailAndPassword(
                request.login(),
                request.password()
        );

        // Générer un JWT pour cet user
        String token = jwtService.generateToken(user);

        // Retourner token + infos user
        return new AuthResponse(token, user);
    }
}
