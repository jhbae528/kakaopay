package com.kakaopay.payments.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    OMISSION_REQUIRED_PARAM(HttpStatus.BAD_REQUEST.value(), "C001", "Omission Required Parameter."),
    NOT_FOUND_ORIGIN_MANAGE_ID(HttpStatus.BAD_REQUEST.value(), "C002", "Not Found Origin ManageId."),
    INVALID_VAT_VALUE(HttpStatus.BAD_REQUEST.value(), "C003", "Invalid VAT Value."),
    INVALID_CANCEL_AMOUNT_DATA(HttpStatus.BAD_REQUEST.value(), "C004", "Invalid Cancel Amount Data."),
    ;

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
