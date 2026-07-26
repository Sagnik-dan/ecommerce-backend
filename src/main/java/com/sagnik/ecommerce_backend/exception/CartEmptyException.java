package com.sagnik.ecommerce_backend.exception;

public class CartEmptyException extends RuntimeException{
    public CartEmptyException(String message){
        super(message);
    }
}
