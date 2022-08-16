package com.fakng.fakngagrgtr.rest.enumiration;

public enum FakngError {


    VACANCY_NOT_FOUND("011", "Vacancy with id=%s not found."),
    VACANCY_ALREADY_EXIST("012", "Vacancy with id=%s is already in the library."),


    COMPANY_NOT_FOUND("021", "Company with id=%s not found."),
    COMPANY_ALREADY_EXIST("022", "Company with id=%s is already in the library."),


    LOCATION_NOT_FOUND("031", "Location with id=%s not found."),
    LOCATION_ALREADY_EXIST("032", "Location with id=%s is already in the library."),


    TECHNICAL_ERROR("001", "A technical error has occurred."),
    VALIDATION_ERROR("002", "Request validation error."),
    URL_NOT_FOUND("003", "Url=%s not found.");

    private final String code;
    private final String message;

    FakngError(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "{" +
                "code: '" + code + '\'' +
                ", message: '" + message + '\'' +
                '}';
    }
}
