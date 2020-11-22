package com.kakopay.payments.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakopay.payments.api.dto.AmountInfo;
import com.kakopay.payments.api.dto.CardInfo;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MainController.class)
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getTest() throws Exception{

        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<String, String>();
        mvm.add("name", "bae");
        mvm.add("id", "123");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/get_test").contentType(MediaType.APPLICATION_JSON).params(mvm);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String content = result.getResponse().getContentAsString();
    }

    @Test
    public void reqPay() throws Exception{
        Map<String, Object> map = new HashMap<>();
        map.put("cardNumber", "1234567812345678");
        map.put("expirationDate", "1120");
        map.put("cvc", "987");
        map.put("installment", 1);
        map.put("amount", 1000);
        map.put("vat", 10);

        String mapJson = objectMapper.writeValueAsString(map);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/reqPay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapJson);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String content2 = result.getResponse().getContentAsString();



        //        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<String, String>();
//        mvm.add("cardNumber", "1234567812345678");
//        mvm.add("expirationDate", "1120");
//        mvm.add("cvc", "987");
//        mvm.add("installment", "0");
//        mvm.add("amount", "0");
//        mvm.add("vat", "0");

        //RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/reqPay").contentType(MediaType.APPLICATION_JSON).params(mvm);

//        CardInfo cardInf = CardInfo.builder()
//            .cardNumber("1234567812345678")
//            .expirationDate("1120")
//            .cvc("987")
//            .build();

//        CardInfo cardInf = CardInfo.builder()
//                .cardNumber("1234567812345678")
//                .expirationDate("1120")
//                .cvc("987")
//                .build();
//
//        AmountInfo amountInfo = AmountInfo.builder()
//                .installment(0)
//                .amount(1000)
//                .vat(10)
//                .build();
//
//        Map cardInfoMap = objectMapper.convertValue(cardInf, Map.class);
//        Map amountInfoMap = objectMapper.convertValue(amountInfo, Map.class);
//        cardInfoMap.putAll(amountInfoMap);
//
//        String infoJson = objectMapper.writeValueAsString(cardInfoMap);
//        System.out.println("infoJson = " + infoJson);

    }
}