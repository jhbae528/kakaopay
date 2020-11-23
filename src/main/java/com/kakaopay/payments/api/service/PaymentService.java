package com.kakaopay.payments.api.service;

import com.kakaopay.payments.api.domain.entity.PaymentInfo;
import com.kakaopay.payments.api.dto.RequestDto;
import com.kakaopay.payments.api.dto.ResponseDto;

import java.util.List;

public interface PaymentService {

    public ResponseDto doPayment(RequestDto requestDto);

    public ResponseDto doCancel(RequestDto requestDto);

    public ResponseDto doReadData(String manageId);

    public List<PaymentInfo> doPaymentList();

}
