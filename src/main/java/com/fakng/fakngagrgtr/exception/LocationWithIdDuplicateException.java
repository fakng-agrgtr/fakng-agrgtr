package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class LocationWithIdDuplicateException extends FakngException {
    public LocationWithIdDuplicateException(Long id) {
        super(FakngError.LOCATION_ALREADY_EXIST, Long.toString(id));
    }
}
