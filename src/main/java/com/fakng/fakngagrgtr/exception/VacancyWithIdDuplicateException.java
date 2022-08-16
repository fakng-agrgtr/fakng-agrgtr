package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class VacancyWithIdDuplicateException extends FakngException {
    public VacancyWithIdDuplicateException(Long id) {
        super(FakngError.VACANCY_ALREADY_EXIST, Long.toString(id));
    }
}
