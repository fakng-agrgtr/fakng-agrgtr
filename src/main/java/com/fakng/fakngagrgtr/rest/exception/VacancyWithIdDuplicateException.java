package com.fakng.fakngagrgtr.rest.exception;

import com.fakng.fakngagrgtr.rest.enumiration.FakngError;

public class VacancyWithIdDuplicateException extends FakngException {
    public VacancyWithIdDuplicateException(Long id) {
        super(FakngError.VACANCY_ALREADY_EXIST, Long.toString(id));
    }
}
