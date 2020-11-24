package com.kakaopay.payments.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class MainControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(MainControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    //@Test
    public void reqPay() throws Exception {

        Map<String, Object> respMap = requestPayment(mapCase1Pay1);
        logger.debug(respMap.toString());

        Assert.assertEquals(200, respMap.get("status"));
        Assert.assertEquals("P0000000000000000001", respMap.get("manageId"));
        Assert.assertNotNull(respMap.get("payStatement"));
    }


    @Test
    public void reqCancel() throws Exception {

        Map<String, Object> respMap = requestPayment(mapCase1Pay1);

        Assert.assertEquals(200, respMap.get("status"));
        Assert.assertEquals("P0000000000000000001", respMap.get("manageId"));
        Assert.assertNotNull(respMap.get("payStatement"));

        Map<String, Object> reqCancelMap = new HashMap<String, Object>(mapCase1Cancel1);
        reqCancelMap.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap = requestCancel(reqCancelMap);

        Assert.assertEquals(200, respCancelMap.get("status"));
        Assert.assertEquals("P0000000000000000002", respCancelMap.get("manageId"));
        Assert.assertNotNull(respCancelMap.get("payStatement"));

        requestPaymentList();
    }

    //@Test
    public void reqReadData() throws Exception {

        Map<String, Object> respMap = requestPayment(mapCase1Pay1);

        Assert.assertEquals(200, respMap.get("status"));
        Assert.assertEquals("P0000000000000000001", respMap.get("manageId"));
        Assert.assertNotNull(respMap.get("payStatement"));

        Map<String, Object> reqReadMap = new HashMap<String, Object>();
        reqReadMap.put("manageId", respMap.get("manageId"));
        Map<String, Object> respReadMap = requestReadData(reqReadMap);

        Assert.assertEquals(200, respReadMap.get("status"));
        Assert.assertEquals("P0000000000000000001", respReadMap.get("manageId"));
        Assert.assertEquals("PAYMENT", respReadMap.get("payType"));
        Assert.assertNotNull(respReadMap.get("cardInfo"));
        Assert.assertNotNull(respReadMap.get("amountInfo"));

        Map<String, Object> reqCancelMap = new HashMap<String, Object>(mapCase1Cancel1);
        reqCancelMap.put("manageId", respReadMap.get("manageId"));
        Map<String, Object> respCancelMap = requestCancel(reqCancelMap);

        Map<String, Object> reqReadCancelMap = new HashMap<String, Object>();
        reqReadCancelMap.put("manageId", respCancelMap.get("manageId"));
        Map<String, Object> respReadCancelMap = requestReadData(reqReadCancelMap);
    }

    //@Test
    public void reqReadList() throws Exception {

        // 첫번째 결제
        Map<String, Object> respMap = requestPayment(mapCase1Pay1);

        // 첫번째 취소
        Map<String, Object> reqCancelMap = new HashMap<String, Object>(mapCase1Cancel1);
        reqCancelMap.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap = requestCancel(reqCancelMap);

        // 두번째 취소
        Map<String, Object> reqCancelMap2 = new HashMap<String, Object>(mapCase1Cancel2);
        reqCancelMap2.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap2 = requestCancel(reqCancelMap2);


        // 두번째 결제
        Map<String, Object> respMap2 = requestPayment(mapCase2Pay1);

        // 첫번째 취소
        Map<String, Object> reqCancelMap3 = new HashMap<String, Object>(mapCase2Cancel1);
        reqCancelMap3.put("manageId", respMap2.get("manageId"));
        Map<String, Object> respCancelMap3 = requestCancel(reqCancelMap3);

        requestPaymentList();
    }


    private Map<String, Object> requestCommWithMap(String requestJson, String url)  throws Exception{

        MockHttpServletResponse response = requestComm(requestJson, url);
        String responseJson = response.getContentAsString();

        Map<String, Object> responseMap = objectMapper.readValue(responseJson, new TypeReference<Map<String, Object>>() {});
        if (response.getStatus() == HttpStatus.OK.value()) {
            responseMap.put("status", response.getStatus());
        }
        return responseMap;
    }

    private List<Map<String, Object>> requestCommWithList(String requestJson, String url)  throws Exception{

        MockHttpServletResponse response = requestComm(requestJson, url);
        String responseJson = response.getContentAsString();

        List<Map<String, Object>> responseList = objectMapper.readValue(responseJson, new TypeReference<List<Map<String, Object>>>() {});
        return responseList;
    }

    // 통신 메서드
    private MockHttpServletResponse requestComm(String requestJson, String url) throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson);

        MvcResult result = mockMvc.perform(requestBuilder)
                // .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        return result.getResponse();
    }

    private Map<String, Object> requestPayment(Map<String, Object> reqMap) throws Exception {

        Map<String, Object> responseMap = requestCommWithMap(objectMapper.writeValueAsString(reqMap), "/reqPayment");
        logger.debug("###  responseMap = " + responseMap.toString());
        return responseMap;
    }

    private Map<String, Object> requestCancel(Map<String, Object> reqMap) throws Exception {

        Map<String, Object> responseMap = requestCommWithMap(objectMapper.writeValueAsString(reqMap), "/reqCancel");
        logger.debug("###  responseMap = " + responseMap.toString());
        return responseMap;
    }

    private Map<String, Object> requestReadData(Map<String, Object> reqMap) throws Exception {

        Map<String, Object> responseMap = requestCommWithMap(objectMapper.writeValueAsString(reqMap), "/reqReadData");
        logger.debug("###  responseMap = " + responseMap.toString());
        return responseMap;
    }

    private String requestPaymentList() throws Exception {

        List<Map<String, Object>> responseList = requestCommWithList("", "/reqPaymentList");
        logger.debug("###  responseList = " + responseList.toString());
        return null;
    }


    Map<String, Object> mapCase1Pay1 = Map.of(
            "cardNumber", "1234567812345678",
            "expirationDate", "1120",
            "cvc", "987",
            "installment", 0,
            "amount", 11000,
            "vat", 1000
    );
    Map<String, Object> mapCase1Cancel1 = Map.of(
            "amount", 1100,
            "vat", 100
    );

    Map<String, Object> mapCase1Cancel2 = Map.of(
            "amount", 3300
    );

    Map<String, Object> mapCase1Cancel3 = Map.of(
            "amount", 7000
    );

    Map<String, Object> mapCase1Cancel4 = Map.of(
            "amount", 6600,
            "vat", 700
    );

    Map<String, Object> mapCase1Cancel5 = Map.of(
            "amount", 6600,
            "vat", 600
    );

    Map<String, Object> mapCase1Cancel6 = Map.of(
            "amount", 100
    );

    Map<String, Object> mapCase2Pay1 = Map.of(
            "cardNumber", "5678123456781234",
            "expirationDate", "0125",
            "cvc", "512",
            "installment", 1,
            "amount", 20000
    );
    Map<String, Object> mapCase2Cancel1 = Map.of(
            "amount", 13000
    );
}