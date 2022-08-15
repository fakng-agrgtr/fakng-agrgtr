package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class DuplicateCompanyException extends FakngException {
    public DuplicateCompanyException() {
        super(FakngError.COMPANY_ALREADY_EXIST);
    }
}
