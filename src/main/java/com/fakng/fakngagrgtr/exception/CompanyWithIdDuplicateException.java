package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class CompanyWithIdDuplicateException extends FakngException {
    public CompanyWithIdDuplicateException(Long id) {
        super(FakngError.COMPANY_ALREADY_EXIST, Long.toString(id));
    }
}
