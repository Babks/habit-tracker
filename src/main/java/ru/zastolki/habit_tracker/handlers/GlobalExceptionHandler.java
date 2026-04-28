package ru.zastolki.habit_tracker.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> notValidArgumentExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation warn: " + ex.getMessage());

        var result = new StringBuilder("Validation problems:\n");

        for (var error : ex.getAllErrors()) {
            result.append('\t');
            var fieldError = (FieldError) error;
            result.append(fieldError.getField());
            result.append('\t');
            result.append(fieldError.getDefaultMessage());
            result.append('\n');
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(result.toString());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unexpected error: " + ex);
    }

}
