package com.kakaopay.payments.api.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.payments.api.domain.entity.PaymentInfo;
import com.kakaopay.payments.api.dto.RequestDto;
import com.kakaopay.payments.api.dto.ResponseDto;
import com.kakaopay.payments.api.service.PaymentService;
import com.kakaopay.payments.api.util.Constants;
import com.kakaopay.payments.api.dto.AmountInfo;
import com.kakaopay.payments.api.dto.CardInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/get_test")
    public String getTest(@RequestParam String name, @RequestParam String id) {
        logger.debug("##############  here !! ");
        return "getTest = " + name + ", " + id;
    }

    // 결제 API
    @PostMapping("/reqPayment")
    public String requestPayment(@RequestBody String reqParam) {

        AmountInfo amountInfo = null;
        CardInfo cardInfo = null;
        try {
            amountInfo = objectMapper.readValue(reqParam, AmountInfo.class);
            cardInfo = objectMapper.readValue(reqParam, CardInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestDto requestDto = RequestDto.builder()
                .cardInfo(cardInfo)
                .amountInfo(amountInfo)
                .payType(Constants.PAY_APPROVE)
                .build();

        ResponseDto responseDto = paymentService.doPayment(requestDto);
        logger.debug("###  responseDto = " + responseDto.toString());

        String response = null;
        try {
            response = objectMapper.writeValueAsString(responseDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    // 결제취소 API
    @PostMapping("/reqCancel")
    public String requestCancel(@RequestBody String reqParam) {

        String manageId = null;
        AmountInfo amountInfo = null;
        try {
            amountInfo = objectMapper.readValue(reqParam, AmountInfo.class);
            Map<String, Object> requestMap = objectMapper.readValue(reqParam, new TypeReference<Map<String, Object>>(){});
            manageId = (String)requestMap.get("manageId");
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestDto requestDto = RequestDto.builder()
                .manageId(manageId)
                .amountInfo(amountInfo)
                .payType(Constants.PAY_CANCEL)
                .build();

        ResponseDto responseDto = paymentService.doCancel(requestDto);
        logger.debug("###  responseDto = " + responseDto.toString());

        String response = null;
        try {
            response = objectMapper.writeValueAsString(responseDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    // 데이터 조회 API
    @PostMapping("/reqReadData")
    public String requestData(@RequestBody String reqParam) {
        return "hello first";
    }

    // 결제 목록 조회 API
    @PostMapping("/reqPaymentList")
    public String requestPaymentList() {
        List<PaymentInfo> paymentInfoList = paymentService.doPaymentList();

        String response = null;
        try{
            response = objectMapper.writeValueAsString(paymentInfoList);
        }catch(Exception e){
            e.printStackTrace();
        }
        return response;
    }

}
