package com.fakng.fakngagrgtr.rest.exception;

public enum ErrorMessage {

    VACANCY_NOT_FOUND("011", "Vacancy with id=%s not found."),
    COMPANY_NOT_FOUND("021", "Company with id=%s not found."),
    LOCATION_NOT_FOUND("031", "Location with id=%s not found."),

    TECHNICAL_ERROR("001", "A technical error has occurred."),
    VALIDATION_ERROR("002", "Request validation error.");

    private final String code;
    private final String message;

    ErrorMessage(final String code, final String message) {
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
