package com.fakng.fakngagrgtr.rest.vacancy;

import com.fakng.fakngagrgtr.rest.exception.ErrorMessage;
import com.fakng.fakngagrgtr.rest.exception.FakngException;

public class VacancyWithIdNotFoundException extends FakngException {

    public VacancyWithIdNotFoundException(Long id) {
        super(ErrorMessage.VACANCY_NOT_FOUND, Long.toString(id));
    }
}
