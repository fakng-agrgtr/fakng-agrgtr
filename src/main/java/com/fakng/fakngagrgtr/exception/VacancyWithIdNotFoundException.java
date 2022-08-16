package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class VacancyWithIdNotFoundException extends FakngException {

    public VacancyWithIdNotFoundException(Long id) {
        super(FakngError.VACANCY_NOT_FOUND, Long.toString(id));
    }

}
