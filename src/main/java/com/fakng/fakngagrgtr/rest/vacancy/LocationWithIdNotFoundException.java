package com.fakng.fakngagrgtr.rest.vacancy;

import com.fakng.fakngagrgtr.rest.exception.ErrorMessage;
import com.fakng.fakngagrgtr.rest.exception.FakngException;

public class LocationWithIdNotFoundException extends FakngException {

    public LocationWithIdNotFoundException(Long id) {
        super(ErrorMessage.LOCATION_NOT_FOUND, Long.toString(id));
    }
}
