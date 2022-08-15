package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class CompanyNotFoundException extends FakngException {

    public CompanyNotFoundException() {
        super(FakngError.COMPANY_NOT_FOUND);
    }

}
