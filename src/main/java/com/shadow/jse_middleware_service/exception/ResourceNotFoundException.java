package com.shadow.jse_middleware_service.exception;

public class ResourceNotFoundException extends RuntimeException  {

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
