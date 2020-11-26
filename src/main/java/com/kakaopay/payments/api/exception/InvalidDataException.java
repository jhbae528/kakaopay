package com.kakaopay.payments.api.exception;

public class InvalidDataException extends CustomException{

    public InvalidDataException(ErrorCode errorCode) {
        super(errorCode);
    }
}
