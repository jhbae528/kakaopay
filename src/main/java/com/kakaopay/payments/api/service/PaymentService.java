package com.kakaopay.payments.api.service;

import com.kakaopay.payments.api.domain.entity.PaymentInfo;
import com.kakaopay.payments.api.dto.RequestDto;
import com.kakaopay.payments.api.dto.ResponseDto;

import java.util.List;

public interface PaymentService {

    public ResponseDto processPayment(RequestDto requestDto) throws Exception;

    public ResponseDto processCancel(RequestDto requestDto) throws Exception;

    public ResponseDto processReadPayment(String manageId) throws Exception;

    public List<PaymentInfo> processReadPaymentList() throws Exception;

}
