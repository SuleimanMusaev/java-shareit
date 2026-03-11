package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final ValidationException e) {
        return new ErrorResponse("error " + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundException(final NotFoundException e) {
        return new ErrorResponse("error " + e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccess(AccessDeniedException e) {
        return new ErrorResponse("error" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflictException(ConflictException e) {
        return new ErrorResponse("error" + e.getMessage());
    }
}
