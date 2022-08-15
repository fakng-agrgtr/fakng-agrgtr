package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class DuplicateLocationException extends FakngException {
    public DuplicateLocationException() {
        super(FakngError.LOCATION_ALREADY_EXIST);
    }
}
