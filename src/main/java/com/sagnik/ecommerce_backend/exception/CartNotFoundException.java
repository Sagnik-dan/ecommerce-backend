package com.sagnik.ecommerce_backend.exception;

public class CartNotFoundException extends RuntimeException{
        public CartNotFoundException(String message){
            super(message);
        }
}
