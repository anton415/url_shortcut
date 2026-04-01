package com.serdyuchenko.urlshortcut.controller;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.serdyuchenko.urlshortcut.exception.ShortcutNotFoundException;

/**
 * Преобразует доменные ошибки контроллеров в ожидаемые HTTP-статусы.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleBadCredentials(BadCredentialsException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(ShortcutNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleShortcutNotFound(ShortcutNotFoundException exception) {
        return Map.of("message", exception.getMessage());
    }
}
