package com.fakng.fakngagrgtr.rest.exception;

import com.fakng.fakngagrgtr.rest.enumiration.FakngError;

public class CompanyWithIdDuplicateException extends FakngException {
    public CompanyWithIdDuplicateException(Long id) {
        super(FakngError.COMPANY_ALREADY_EXIST, Long.toString(id));
    }
}
