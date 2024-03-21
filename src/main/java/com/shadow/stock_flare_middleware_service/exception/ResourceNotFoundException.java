package com.shadow.stock_flare_middleware_service.exception;

public class ResourceNotFoundException extends RuntimeException  {

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
