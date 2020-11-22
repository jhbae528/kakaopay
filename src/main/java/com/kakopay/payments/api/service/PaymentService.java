package com.kakopay.payments.api.service;

import com.kakopay.payments.api.dto.RequestDto;
import com.kakopay.payments.api.dto.ResponseDto;

public interface PaymentService {

    public ResponseDto doPayment(RequestDto requestDto);

    public ResponseDto doCancel(RequestDto requestDto);

    public ResponseDto doReadData(String manageId);

}
