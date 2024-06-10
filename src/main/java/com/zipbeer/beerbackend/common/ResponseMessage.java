package com.zipbeer.beerbackend.common;

public interface ResponseMessage {
    String SUCCESS = "Success.";
    String VALIDATION_FAIL = "Validation failed.";
    String DUPLICATE_ID = "Duplicate Id.";
    String SING_IN_FAIL = "Login information mismatch.";
    String CERTIFICATION_FAIL = "Certification failed.";
    String DATABASE_ERROR = "Database error.";
    String MAIL_FAIL = "Mail send failed.";
    String NOT_EXIST_MAIL = "Mail not exist";
}
