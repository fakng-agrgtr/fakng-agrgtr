package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class LocationWithIdNotFoundException extends FakngException {

    public LocationWithIdNotFoundException(Long id) {
        super(FakngError.LOCATION_NOT_FOUND, Long.toString(id));
    }

}
