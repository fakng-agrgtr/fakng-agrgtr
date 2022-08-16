package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public abstract class FakngException extends RuntimeException {

    protected FakngException(FakngError error, String attribute) {
        super(String.format(error.getMessage(), attribute));
    }
}
