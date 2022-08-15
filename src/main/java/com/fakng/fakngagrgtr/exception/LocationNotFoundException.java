package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class LocationNotFoundException extends FakngException {

    public LocationNotFoundException() {
        super(FakngError.LOCATION_NOT_FOUND);
    }

}
