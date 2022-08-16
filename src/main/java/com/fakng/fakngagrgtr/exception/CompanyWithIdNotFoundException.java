package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class CompanyWithIdNotFoundException extends FakngException {

    public CompanyWithIdNotFoundException(Long id) {
        super(FakngError.COMPANY_NOT_FOUND, Long.toString(id));
    }

}
