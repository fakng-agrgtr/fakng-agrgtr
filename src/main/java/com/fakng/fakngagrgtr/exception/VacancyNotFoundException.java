package com.fakng.fakngagrgtr.exception;

import com.fakng.fakngagrgtr.enumiration.FakngError;

public class VacancyNotFoundException extends FakngException {

    public VacancyNotFoundException() {
        super(FakngError.VACANCY_NOT_FOUND);
    }

}
