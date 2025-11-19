package com.obs.productmanagement.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }

    public UserNotFoundException(String username, String password) {
        super("User with name " + username + " or password " + password + " not found");
    }
}
