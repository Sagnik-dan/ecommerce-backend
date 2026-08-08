package com.sagnik.ecommerce_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleUserExists(
            UserAlreadyExistsException ex) {

        return Map.of(
                "error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        return Map.of(
                "error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleCategoryExists(
            CategoryAlreadyExistsException ex) {

        return Map.of(
                "error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,String> handleProductNotFound(
            ProductNotFoundException ex){

        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(CartNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCartNotFound(
            CartNotFoundException ex) {

        return Map.of(
                "error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCartItemNotFound(
            CartItemNotFoundException ex) {

        return Map.of(
                "error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFound(
            UserNotFoundException ex) {

        return Map.of(
                "error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInsufficientStock(
            InsufficientStockException ex) {

        return Map.of(
                "error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(CartEmptyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleCartIsEmpty(
            CartEmptyException ex) {

        return Map.of(
                "error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleOrderNotFound(
            OrderNotFoundException ex) {

        return Map.of(
                "error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidOrderState(
            InvalidOrderStateException ex) {

        return Map.of(
                "error",
                ex.getMessage()
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCategoryNotFound(
            CategoryNotFoundException ex) {

        return Map.of(
                "error",
                ex.getMessage()
        );
    }
}
