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

@Slf4j
@RestControllerAdvice
class CustomControllerAdvice {


    @ExceptionHandler(FakngException.class)
    public ResponseEntity<String> handleLibraryExceptions(FakngException e) {
        logStackTrace(e);
        return new ResponseEntity<>(
                e.getMessage(),
                HttpStatus.valueOf(400)
        );
    }

    @ExceptionHandler({ValidationException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<String> handleJsonExceptions(Exception e) {
        logStackTrace(e);
        return new ResponseEntity<>(
                FakngError.VALIDATION_ERROR.toString(),
                HttpStatus.valueOf(400)
        );
    }

    @ExceptionHandler
    public ResponseEntity<String> handleOtherExceptions(Exception e) {
        logStackTrace(e);
        return new ResponseEntity<>(
                FakngError.TECHNICAL_ERROR.toString(),
                HttpStatus.valueOf(500)
        );
    }

    private void logStackTrace(Exception e) {
        for (StackTraceElement element : e.getStackTrace())
            log.error(element.toString());
    }
}
