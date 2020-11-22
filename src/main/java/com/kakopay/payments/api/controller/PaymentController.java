package com.kakopay.payments.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakopay.payments.api.dto.AmountInfo;
import com.kakopay.payments.api.dto.CardInfo;
import com.kakopay.payments.api.dto.RequestDto;
import com.kakopay.payments.api.dto.ResponseDto;
import com.kakopay.payments.api.service.PaymentService;
import com.kakopay.payments.api.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class PaymentController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentService paymentService;


    @GetMapping("/get_test")
    public String getTest(@RequestParam String name, @RequestParam String id) {
        System.out.println("here!!!");
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

        ResponseDto temp = paymentService.doPayment(requestDto);

        ResponseDto responseDto = ResponseDto.builder()
                .manageId("P00000001")
                .payStatement("ABCDE")
                .build();


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

        return null;
    }

    // 데이터 조회 API
    @GetMapping("/reqReadData")
    public String requestData(@RequestBody String reqParam) {
        return "hello first";
    }

}
