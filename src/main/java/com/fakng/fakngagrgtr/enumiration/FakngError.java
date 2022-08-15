package com.fakng.fakngagrgtr.enumiration;

public enum FakngError {


    VACANCY_NOT_FOUND("011", "Vacancy not found."),
    VACANCY_ALREADY_EXIST("012", "This vacancy is already in the library."),


    COMPANY_NOT_FOUND("021", "Company not found."),
    COMPANY_ALREADY_EXIST("022", "This company is already in the library."),


    LOCATION_NOT_FOUND("031", "Location not found."),
    LOCATION_ALREADY_EXIST("032", "This location is already in the library."),


    TECHNICAL_ERROR("001", "A technical error has occurred."),
    VALIDATION_ERROR("002", "Request validation error."),
    URL_FOUND("003", "Url not found.");

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
