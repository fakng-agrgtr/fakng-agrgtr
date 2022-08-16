package com.fakng.fakngagrgtr.rest.exception;

import com.fakng.fakngagrgtr.rest.enumiration.FakngError;

public class CompanyWithIdNotFoundException extends FakngException {

    public CompanyWithIdNotFoundException(Long id) {
        super(FakngError.COMPANY_NOT_FOUND, Long.toString(id));
    }

}
