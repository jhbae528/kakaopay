package com.kakaopay.payments.api.exception;

public class InvalidDataException extends CustomException{

    public InvalidDataException() {
        super(ErrorCode.INVALID_INPUT_VALUE);
    }
}
