package com.fakng.fakngagrgtr.rest.exception;

import com.fakng.fakngagrgtr.rest.enumiration.FakngError;

public class LocationWithIdNotFoundException extends FakngException {

    public LocationWithIdNotFoundException(Long id) {
        super(FakngError.LOCATION_NOT_FOUND, Long.toString(id));
    }

}
