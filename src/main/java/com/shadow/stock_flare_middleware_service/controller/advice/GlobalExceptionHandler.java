package com.shadow.stock_flare_middleware_service.controller.advice;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.shadow.stock_flare_middleware_service.controller.response.Error;
import com.shadow.stock_flare_middleware_service.controller.response.ErrorResponse;
import com.shadow.stock_flare_middleware_service.exception.RequestDateRangeException;
import com.shadow.stock_flare_middleware_service.exception.ResourceConflictException;
import com.shadow.stock_flare_middleware_service.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(Exception validException, HttpServletRequest request){
        List<Error> errors = new ArrayList<>();

        if (validException instanceof ConstraintViolationException) {
            ((ConstraintViolationException) validException).getConstraintViolations()
                    .forEach(violation ->
                            errors.add(new Error("Validation Error",
                                    String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                            )
                    );
        } else if (validException instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) validException;
            exception.getBindingResult()
                    .getFieldErrors()
                    .forEach(error -> errors.add(new Error("Validation Error", String.format("Field: %s - %s", error.getField(), error.getDefaultMessage()))));
        } else if (validException.getCause() instanceof InvalidFormatException){
            InvalidFormatException formatException = (InvalidFormatException) validException.getCause();
            errors.add(new Error("Field Format Error", String.format("Invalid value provided [%s]", formatException.getValue())));
        } else if (validException instanceof MethodArgumentTypeMismatchException) {
            MethodArgumentTypeMismatchException mismatchException = (MethodArgumentTypeMismatchException) validException;
            errors.add(new Error("Invalid Data Error", String.format("Field: %s Invalid value provided %s", mismatchException.getName(), mismatchException.getValue())));
        } else {
            errors.add(new Error("Bad Request", "Unable to process request"));
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
        } else if (exception instanceof RequestDateRangeException) {
            return HttpStatus.BAD_REQUEST;
        }else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
