package com.fakng.fakngagrgtr.rest.vacancy;

import com.fakng.fakngagrgtr.rest.exception.ErrorMessage;
import com.fakng.fakngagrgtr.rest.exception.FakngException;

public class CompanyWithIdNotFoundException extends FakngException {

    public CompanyWithIdNotFoundException(Long id) {
        super(ErrorMessage.COMPANY_NOT_FOUND, Long.toString(id));
    }
}
