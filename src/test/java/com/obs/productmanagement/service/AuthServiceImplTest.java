package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.AuthResponse;
import com.obs.productmanagement.dto.LoginRequest;
import com.obs.productmanagement.dto.UserResponse;
import com.obs.productmanagement.exception.UserNotFoundException;
import com.obs.productmanagement.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private IUserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
        // GIVEN
        LoginRequest request = new LoginRequest("salem@example.com", "secret123");

        UserResponse user = new UserResponse(1L, "Salem", 25, "salem@example.com");
        String token = "jwt-token";

        when(userService.getUserByNameOrEmailAndPassword("salem@example.com", "secret123"))
                .thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(token);

        // WHEN
        AuthResponse result = authService.login(request);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(token);
        assertThat(result.user()).isEqualTo(user);

        verify(userService).getUserByNameOrEmailAndPassword("salem@example.com", "secret123");
        verify(jwtService).generateToken(user);
        verifyNoMoreInteractions(userService, jwtService);
    }

    @Test
    void login_shouldPropagateException_whenUserServiceFails() {
        // GIVEN
        LoginRequest request = new LoginRequest("unknown@example.com", "wrong");

        when(userService.getUserByNameOrEmailAndPassword("unknown@example.com", "wrong"))
                .thenThrow(new UserNotFoundException("unknown@example.com", "wrong"));

        // WHEN + THEN
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UserNotFoundException.class);

        verify(userService).getUserByNameOrEmailAndPassword("unknown@example.com", "wrong");
        verifyNoInteractions(jwtService); // IMPORTANT : pas de token si login KO
    }
}
