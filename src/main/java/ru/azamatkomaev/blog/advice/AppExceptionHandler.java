package ru.azamatkomaev.blog.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.azamatkomaev.blog.exception.NotFoundException;
import ru.azamatkomaev.blog.exception.UnauthorizedException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AppExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, String> handleNotFoundException(NotFoundException ex) {
        return new HashMap<>(
            Map.of("message", ex.getMessage())
        );
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public Map<String, String> handleUnauthorizedException(UnauthorizedException ex) {
        return new HashMap<>(
            Map.of("message", ex.getMessage())
        );
    }
}
