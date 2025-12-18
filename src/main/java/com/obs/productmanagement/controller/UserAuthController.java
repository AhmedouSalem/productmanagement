package com.obs.productmanagement.controller;

import com.obs.productmanagement.dto.UserCreateRequest;
import com.obs.productmanagement.dto.UserResponse;
import com.obs.productmanagement.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAuthController {

    private final IUserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse created = userService.createUser(request);

        // renvoyer un 201 Created avec Location
        URI location = URI.create("/api/users/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    // JWT requis
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

}

