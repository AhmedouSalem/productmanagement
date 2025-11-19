package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.UserCreateRequest;
import com.obs.productmanagement.dto.UserResponse;
import com.obs.productmanagement.dto.mapper.UserMapper;
import com.obs.productmanagement.exception.UserAlreadyExistsException;
import com.obs.productmanagement.exception.UserNotFoundException;
import com.obs.productmanagement.model.User;
import com.obs.productmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserCreateRequest createRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        createRequest = new UserCreateRequest("Salem", 25, "salem@test.com", "secret");
        user = new User();
        user.setId(1L);
        user.setName("Salem");
        user.setAge(25);
        user.setEmail("salem@test.com");
        user.setPassword("secret");

        userResponse = new UserResponse(1L, "Salem", 25, "salem@test.com");
    }

    @Test
    void createUser_shouldSaveAndReturnUser_whenEmailDoesNotExist() {
        // GIVEN
        when(userRepository.existsByEmail(createRequest.email())).thenReturn(false);
        when(userMapper.toEntity(createRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // WHEN
        UserResponse result = userService.createUser(createRequest);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.email()).isEqualTo("salem@test.com");

        verify(userRepository).existsByEmail("salem@test.com");
        verify(userRepository).save(user);
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {
        // GIVEN
        when(userRepository.existsByEmail(createRequest.email())).thenReturn(true);

        // WHEN + THEN
        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        // GIVEN
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // WHEN
        UserResponse result = userService.getUserById(1L);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Salem");
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_shouldThrowException_whenNotFound() {
        // GIVEN
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserByNameOrEmailAndPassword_shouldSearchByEmail_whenLoginContainsAtSign() {
        // GIVEN
        String login = "salem@test.com";
        String password = "secret";

        when(userRepository.findByEmailAndPassword(login, password)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // WHEN
        UserResponse result = userService.getUserByNameOrEmailAndPassword(login, password);

        // THEN
        assertThat(result.email()).isEqualTo("salem@test.com");
        verify(userRepository).findByEmailAndPassword(login, password);
        verify(userRepository, never()).findByNameAndPassword(anyString(), anyString());
    }

    @Test
    void getUserByNameOrEmailAndPassword_shouldSearchByName_whenLoginDoesNotContainAtSign() {
        // GIVEN
        String login = "salem";
        String password = "secret";

        when(userRepository.findByNameAndPassword(login, password)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        // WHEN
        UserResponse result = userService.getUserByNameOrEmailAndPassword(login, password);

        // THEN
        assertThat(result.name()).isEqualTo("Salem");
        verify(userRepository).findByNameAndPassword(login, password);
        verify(userRepository, never()).findByEmailAndPassword(anyString(), anyString());
    }

    @Test
    void getUserByNameOrEmailAndPassword_shouldThrowException_whenUserNotFound() {
        // GIVEN
        String login = "inconnu@test.com";
        String password = "wrong";

        when(userRepository.findByEmailAndPassword(login, password)).thenReturn(null);

        // WHEN + THEN
        assertThatThrownBy(() -> userService.getUserByNameOrEmailAndPassword(login, password))
                .isInstanceOf(UserNotFoundException.class);
    }
}
