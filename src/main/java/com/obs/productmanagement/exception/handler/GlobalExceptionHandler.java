package com.obs.productmanagement.exception.handler;

import com.obs.productmanagement.dto.ErrorResponse;
import com.obs.productmanagement.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        return new ErrorResponse(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                "USER_NOT_FOUND"
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return new ErrorResponse(
                e.getMessage(),
                HttpStatus.CONFLICT.value(),
                "USER_ALREADY_EXISTS"
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFound(ProductNotFoundException e) {
        return new ErrorResponse(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                "PRODUCT_NOT_FOUND"
        );
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleProductAlreadyExists(ProductAlreadyExistsException e) {
        return new ErrorResponse(
                e.getMessage(),
                HttpStatus.CONFLICT.value(),
                "PRODUCT_ALREADY_EXISTS"
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCategoryNotFound(CategoryNotFoundException e) {
        return new ErrorResponse(
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                "CATEGORY_NOT_FOUND"
        );
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleCategoryAlreadyExists(CategoryAlreadyExistsException e) {
        return new ErrorResponse(
                e.getMessage(),
                HttpStatus.CONFLICT.value(),
                "CATEGORY_ALREADY_EXISTS"
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception e) {
        return new ErrorResponse(
                "Internal server error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR"
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        // concaténer tous les messages d’erreur
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Validation error");

        return new ErrorResponse(
                message,
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR"
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return new ErrorResponse(
                "Invalid value for parameter '" + e.getName() + "'",
                HttpStatus.BAD_REQUEST.value(),
                "TYPE_MISMATCH"
        );
    }

}
