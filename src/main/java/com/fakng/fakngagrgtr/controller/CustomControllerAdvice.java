package com.fakng.fakngagrgtr.controller;

import com.fakng.fakngagrgtr.enumiration.FakngError;
import com.fakng.fakngagrgtr.exception.FakngException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.xml.bind.ValidationException;

/**
 * Централизированное управление ошибками.<br>
 * Все ошибки летят сюда.
 *
 * В любой момент можем добавить другой метод, который ловит определенный тип ошибок
 */
@Slf4j
@RestControllerAdvice
class CustomControllerAdvice {

    /**
     * Если летит наша ошибка
     * @param e выброшенный exception
     * @return ответ с ошибкой 400
     */
    @ExceptionHandler(FakngException.class)
    public ResponseEntity<String> handleLibraryExceptions(FakngException e) {
        log.error(e.getClass().toString());
        log.error(e.getMessage());
        for (StackTraceElement element : e.getStackTrace())
            log.error(element.toString());
        return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.valueOf(400)
        );
    }

    /**
     * Всякие ошибки валидации и т д
     * @param e выброшенный exception
     * @return ответ с ошибкой 400
     */
    @ExceptionHandler({ValidationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<String> handleJsonExceptions(Exception e) {
        log.error(e.getClass().toString());
        log.error(e.getMessage());
        for (StackTraceElement element : e.getStackTrace())
            log.error(element.toString());
        return new ResponseEntity<>(
                FakngError.VALIDATION_ERROR.toString(),
                HttpStatus.valueOf(400)
        );
    }

    /**
     * Все другие ошибки
     * @param e выброшенный exception (не наш)
     * @return ответ с ошибкой 500(тут скорее уже ошибка нашего сервера)
     */
    @ExceptionHandler
    public ResponseEntity<String> handleOtherExceptions(Exception e) {
        log.error(e.getClass().toString());
        log.error(e.getMessage());
        for (StackTraceElement element : e.getStackTrace())
            log.error(element.toString());
        return new ResponseEntity<>(
                FakngError.TECHNICAL_ERROR.toString(),
                HttpStatus.valueOf(500)
        );
    }
}
