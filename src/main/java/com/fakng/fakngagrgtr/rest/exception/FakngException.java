package com.fakng.fakngagrgtr.rest.exception;

import com.fakng.fakngagrgtr.rest.enumiration.FakngError;

public abstract class FakngException extends RuntimeException {

    protected FakngException(FakngError error, String attribute) {
        super(String.format(error.getMessage(), attribute));
    }
}
