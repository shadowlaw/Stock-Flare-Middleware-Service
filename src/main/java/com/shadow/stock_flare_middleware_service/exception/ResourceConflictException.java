package com.shadow.stock_flare_middleware_service.exception;

public class ResourceConflictException extends RuntimeException{
    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
