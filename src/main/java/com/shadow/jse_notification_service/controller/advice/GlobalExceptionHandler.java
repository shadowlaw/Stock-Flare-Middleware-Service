package com.shadow.jse_notification_service.controller.advice;

import com.shadow.jse_notification_service.controller.response.Error;
import com.shadow.jse_notification_service.controller.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException validException, HttpServletRequest request){
        List<Error> errors = new ArrayList<>();

        validException.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.add(new Error("Validation Error", String.format("Field: %s - %s", error.getField(), error.getDefaultMessage()))));
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errors, request.getRequestURI());
        logger.error("Request Validation Error: {}", validException.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleException(RuntimeException exception, HttpServletRequest request) {
        List<Error> errors = new ArrayList<>();
        errors.add(new Error("Error", exception.getMessage()));
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errors, request.getRequestURI());
        logger.error("Request Error: {}", exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
