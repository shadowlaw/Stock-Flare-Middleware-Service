package com.shadow.jse_notification_service.controller.advice;

import com.shadow.jse_notification_service.controller.response.Error;
import com.shadow.jse_notification_service.controller.response.ErrorResponse;
import com.shadow.jse_notification_service.exception.ResourceConflictException;
import com.shadow.jse_notification_service.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(Exception validException, HttpServletRequest request){
        List<Error> errors = new ArrayList<>();

        if (validException instanceof ConstraintViolationException) {
            errors.add(new Error("Validation Error", validException.getMessage()));
        } else {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) validException;
            exception.getBindingResult()
                    .getFieldErrors()
                    .forEach(error -> errors.add(new Error("Validation Error", String.format("Field: %s - %s", error.getField(), error.getDefaultMessage()))));
        }

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors, request.getRequestURI());
        logger.error("Request Validation Error: {}", validException.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleThrownGeneralExceptions(Exception exception, HttpServletRequest request) {
        HttpStatus status = getStatusCode(exception);

        List<Error> errors = new ArrayList<>();
        errors.add(new Error("Error", exception.getMessage()));
        ErrorResponse errorResponse = new ErrorResponse(status.value(), errors, request.getRequestURI());
        logger.error("{}: {}", exception.getClass(), exception.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    private HttpStatus getStatusCode(Exception exception) {
        if (exception instanceof ResourceNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (exception instanceof ResourceConflictException){
            return HttpStatus.CONFLICT;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
