package com.kakaopay.payments.api.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.payments.api.domain.entity.PaymentInfo;
import com.kakaopay.payments.api.dto.AmountInfo;
import com.kakaopay.payments.api.dto.CardInfo;
import com.kakaopay.payments.api.dto.RequestDto;
import com.kakaopay.payments.api.dto.ResponseDto;
import com.kakaopay.payments.api.exception.ErrorCode;
import com.kakaopay.payments.api.exception.InvalidDataException;
import com.kakaopay.payments.api.service.PaymentService;
import com.kakaopay.payments.api.util.Constants;
import com.kakaopay.payments.api.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
public class PaymentController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentService paymentService;


    // 결제 API
    @PostMapping("/reqPayment")
    public String requestPayment(@RequestBody String reqParam) throws Exception {

        AmountInfo amountInfo = objectMapper.readValue(reqParam, AmountInfo.class);
        CardInfo cardInfo = objectMapper.readValue(reqParam, CardInfo.class);

        if(StringUtils.isNull(cardInfo.getCardNumber()))    // 카드번호 존재 검증
            throw new InvalidDataException(ErrorCode.OMISSION_REQUIRED_PARAM);

        RequestDto requestDto = RequestDto.builder()
                .cardInfo(cardInfo)
                .amountInfo(amountInfo)
                .payType(Constants.PAY_APPROVE)
                .build();

        ResponseDto responseDto = paymentService.processPayment(requestDto);
        log.debug("###  responseDto = " + responseDto.toString());

        return objectMapper.writeValueAsString(responseDto);
    }

    // 결제취소 API
    @PostMapping("/reqCancel")
    public String requestCancel(@RequestBody String reqParam) throws Exception {

        Map<String, Object> requestMap = objectMapper.readValue(reqParam, new TypeReference<Map<String, Object>>(){});
        String manageId = (String)requestMap.get("manageId");
        AmountInfo amountInfo = objectMapper.readValue(reqParam, AmountInfo.class);

        if(StringUtils.isNull(manageId))    // 관리번호 존재 검증
            throw new InvalidDataException(ErrorCode.OMISSION_REQUIRED_PARAM);

        RequestDto requestDto = RequestDto.builder()
                .manageId(manageId)
                .amountInfo(amountInfo)
                .payType(Constants.PAY_CANCEL)
                .build();

        ResponseDto responseDto = paymentService.processCancel(requestDto);
        log.debug("###  responseDto = " + responseDto.toString());

        return objectMapper.writeValueAsString(responseDto);
    }

    // 데이터 조회 API
    @PostMapping("/reqReadData")
    public String requestData(@RequestBody String reqParam) throws Exception {

        Map<String, Object> requestMap = objectMapper.readValue(reqParam, new TypeReference<Map<String, Object>>(){});
        String manageId = (String)requestMap.get("manageId");

        if(StringUtils.isNull(manageId))    // 관리번호 존재 검증
            throw new InvalidDataException(ErrorCode.OMISSION_REQUIRED_PARAM);

        ResponseDto responseDto =  paymentService.processReadPayment(manageId);
        log.debug("###  responseDto = " + responseDto.toString());

        return objectMapper.writeValueAsString(responseDto);
    }

    // 결제 목록 조회 API
    @PostMapping("/reqPaymentList")
    public String requestPaymentList() throws Exception {

        List<PaymentInfo> paymentInfoList = paymentService.processReadPaymentList();
        log.debug("###  paymentInfoList = " + paymentInfoList.toString());

        return objectMapper.writeValueAsString(paymentInfoList);
    }

}
