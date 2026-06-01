package ru.zastolki.habit_tracker.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> notValidArgumentExceptions(MethodArgumentNotValidException ex) {
        log.warn("Ошибка валидации входящего запроса: количествоОшибок={}", ex.getErrorCount());

        var result = new StringBuilder("Ошибки валидации:\n");

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentExceptions(IllegalArgumentException ex) {
        log.warn("Бизнес-ошибка при обработке запроса: сообщение={}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> responseStatusExceptions(ResponseStatusException ex) {
        log.warn("Запрос завершен с ожидаемой ошибкой: статус={} причина={}",
                ex.getStatusCode(),
                ex.getReason());

        return ResponseEntity.status(ex.getStatusCode())
                .body(ex.getReason());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        log.error("Непредвиденная ошибка при обработке запроса", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Непредвиденная ошибка при обработке запроса");
    }
}
