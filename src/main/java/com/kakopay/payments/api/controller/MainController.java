package com.kakopay.payments.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakopay.payments.api.dto.AmountInfo;
import com.kakopay.payments.api.dto.CardInfo;
import com.kakopay.payments.api.dto.RequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MainController {

    @Autowired
    private ObjectMapper objectMapper;


    @GetMapping("/get_test")
    public String getTest(@RequestParam String name, @RequestParam String id){
        System.out.println("here!!!");
        return "getTest = " + name + ", " + id;
    }

    // 결제 API
    @PostMapping("/reqPay")
    //public String requestPayment(@RequestBody CardInfo cardInfo){
    //public String requestPayment(@RequestBody Map<String, String> cardInfo){
    public String requestPayment(@RequestBody String reqInfo){

        String response = null;
        try{
            //AmountInfo amountInfo = objectMapper.readValue(reqInfo, AmountInfo.class);
            //CardInfo cardInfo = objectMapper.readValue(reqInfo, CardInfo.class);
            RequestDto requestInfo = objectMapper.readValue(reqInfo, RequestDto.class);

            System.out.println("cardNumber = " + requestInfo.getManageId());

            response = objectMapper.writeValueAsString(requestInfo);

        }catch(Exception e){

        }
        //int b = amountInfo.getAmount();
        //String a = cardInfo.getCardNumber();

        return response;
    }

    // 결제취소 API
    @PostMapping("/reqCancel")
    //public String requestPayment(@RequestBody CardInfo cardInfo){
    public String requestCancel(@RequestBody CardInfo cardInfo){

        //int b = amountInfo.getAmount();
        String a = cardInfo.getCardNumber();

        return null;
    }

    // 데이터 조회 API
    @GetMapping("/reqData")
    public String requestData(@RequestParam String manageId){
        return "hello first";
    }

}
