package com.fakng.fakngagrgtr.rest.exception;

public abstract class FakngException extends RuntimeException {

    protected FakngException(ErrorMessage error, String attribute) {
        super(String.format(error.getMessage(), attribute));
    }
}
