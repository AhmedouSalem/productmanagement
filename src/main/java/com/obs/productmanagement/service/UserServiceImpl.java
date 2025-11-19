package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.UserCreateRequest;
import com.obs.productmanagement.dto.UserResponse;
import com.obs.productmanagement.dto.mapper.UserMapper;
import com.obs.productmanagement.exception.UserAlreadyExistsException;
import com.obs.productmanagement.exception.UserNotFoundException;
import com.obs.productmanagement.model.User;
import com.obs.productmanagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException(request.email());
        }

        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);

        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByNameOrEmailAndPassword(String login, String password) {
        User user = null;

        if (login.contains("@")) {
            user = userRepository.findByEmailAndPassword(login, password);
        } else {
            user = userRepository.findByNameAndPassword(login, password);
        }

        if (user != null) {
            return userMapper.toResponse(user);
        }else {
            throw new UserNotFoundException(login, password);
        }
    }
}
