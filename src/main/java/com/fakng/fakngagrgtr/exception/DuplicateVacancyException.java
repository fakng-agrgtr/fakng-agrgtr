package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class DuplicateVacancyException extends FakngException {
    public DuplicateVacancyException() {
        super(FakngError.VACANCY_ALREADY_EXIST);
    }
}
