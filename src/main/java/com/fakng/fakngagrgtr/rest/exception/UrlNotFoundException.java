package com.fakng.fakngagrgtr.rest.exception;

import com.fakng.fakngagrgtr.rest.enumiration.FakngError;

public class UrlNotFoundException extends FakngException{

    public UrlNotFoundException(String url) {
        super(FakngError.URL_NOT_FOUND, url);
    }
}
