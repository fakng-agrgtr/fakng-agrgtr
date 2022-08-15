package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class UrlNotFoundException extends FakngException{

    public UrlNotFoundException(String url) {
        super(FakngError.URL_NOT_FOUND, url);
    }
}
