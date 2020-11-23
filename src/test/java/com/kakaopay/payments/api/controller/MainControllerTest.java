package com.kakaopay.payments.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class MainControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(MainControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    //@Test
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

    // 통신 메서드
    private String requestComm(String requestJson, String url) throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        return result.getResponse().getContentAsString();
    }

    private String requestPayment() throws Exception{

        Map<String, Object> reqMap = new HashMap<String, Object>();
        reqMap.put("cardNumber", "1234567812345678");
        reqMap.put("expirationDate", "1120");
        reqMap.put("cvc", "987");
        reqMap.put("installment", 0);
        reqMap.put("amount", 10000);

        String requestJson = objectMapper.writeValueAsString(reqMap);

        String responseJson = requestComm(requestJson, "/reqPayment");
        logger.debug("###  responseJson = " + responseJson);

        Map<String, Object> responseMap = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});
        String manageId = (String)responseMap.get("manageId");
        String payStatement = (String)responseMap.get("payStatement");
        logger.debug("###  manageId = " + manageId);
        logger.debug("###  payStatement = " + payStatement);

        return manageId;
    }

    private String requestCancel(String manageId) throws Exception{

        Map<String, Object> reqMap = new HashMap<String, Object>();
        reqMap.put("manageId", manageId);
        reqMap.put("amount", 5000);
        reqMap.put("vat", 400);

        String requestJson = objectMapper.writeValueAsString(reqMap);

        String responseJson = requestComm(requestJson, "/reqCancel");
        logger.debug("###  responseJson = " + responseJson);

        Map<String, Object> responseMap = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});
        String cancelManageId = (String)responseMap.get("manageId");
        String payStatement = (String)responseMap.get("payStatement");
        logger.debug("###  cancelManageId = " + cancelManageId);
        logger.debug("###  payStatement = " + payStatement);

        return manageId;
    }

    private String requestPaymentList() throws Exception {

        String responseJson = requestComm("", "/reqPaymentList");
        logger.debug("###  responseJson = " + responseJson);
        return null;
    }

    @Test
    public void reqPay() throws Exception {

        String manageId = requestPayment();
        String cancelManageId = requestCancel(manageId);
        requestPaymentList();
    }
}