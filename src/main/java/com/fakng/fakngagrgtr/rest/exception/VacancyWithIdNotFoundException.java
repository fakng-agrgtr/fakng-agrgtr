package com.fakng.fakngagrgtr.rest.exception;

import com.fakng.fakngagrgtr.rest.enumiration.FakngError;

public class VacancyWithIdNotFoundException extends FakngException {

    public VacancyWithIdNotFoundException(Long id) {
        super(FakngError.VACANCY_NOT_FOUND, Long.toString(id));
    }

}
