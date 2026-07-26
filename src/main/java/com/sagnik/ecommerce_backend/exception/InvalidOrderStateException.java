package com.sagnik.ecommerce_backend.exception;

public class InvalidOrderStateException extends RuntimeException{
    public InvalidOrderStateException(String message){
        super(message);
    }
}
