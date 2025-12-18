package com.obs.productmanagement.service;

import com.obs.productmanagement.dto.UserCreateRequest;
import com.obs.productmanagement.dto.UserResponse;

public interface IUserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByNameOrEmailAndPassword(String login, String password);
    UserResponse getCurrentUser();
}
