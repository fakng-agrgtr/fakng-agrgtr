package com.fakng.fakngagrgtr.rest.exception;

import com.fakng.fakngagrgtr.rest.enumiration.FakngError;

public class LocationWithIdDuplicateException extends FakngException {
    public LocationWithIdDuplicateException(Long id) {
        super(FakngError.LOCATION_ALREADY_EXIST, Long.toString(id));
    }
}
