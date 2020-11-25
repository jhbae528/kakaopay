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


    //@Test
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

    //@Test
    public void case1Test() throws Exception {

        logger.debug("@@@@@  CASE1 => 결제 [결제금액 : 11,000 / 부가가치세 : 1,000]");
        Map<String, Object> respMap = requestPayment(mapCase1Pay1);

        Assert.assertEquals(200, respMap.get("status"));
        Assert.assertEquals("P0000000000000000001", respMap.get("manageId"));
        Assert.assertNotNull(respMap.get("payStatement"));
        requestReadData(respMap);


        logger.debug("@@@@@  CASE1-1 => 부분취소 [취소금액 : 1,100 / 부가가치세 : 100]");
        Map<String, Object> reqCancelMap = new HashMap<String, Object>(mapCase1Cancel1);
        reqCancelMap.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap = requestCancel(reqCancelMap);

        Assert.assertEquals(200, respCancelMap.get("status"));
        Assert.assertEquals("P0000000000000000002", respCancelMap.get("manageId"));
        Assert.assertNotNull(respCancelMap.get("payStatement"));
        requestReadData(respMap);

        logger.debug("@@@@@  CASE1-2 => 부분취소 [취소금액 : 3,300 / 부가가치세 : null]");
        Map<String, Object> reqCancelMap2 = new HashMap<String, Object>(mapCase1Cancel2);
        reqCancelMap2.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap2 = requestCancel(reqCancelMap2);

        Assert.assertEquals(200, respCancelMap2.get("status"));
        Assert.assertEquals("P0000000000000000003", respCancelMap2.get("manageId"));
        Assert.assertNotNull(respCancelMap2.get("payStatement"));
        requestReadData(respMap);


        logger.debug("@@@@@  CASE1-3 => 부분취소 [취소금액 : 7,000 / 부가가치세 : null]");
        Map<String, Object> reqCancelMap3 = new HashMap<String, Object>(mapCase1Cancel3);
        reqCancelMap3.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap3 = requestCancel(reqCancelMap3);

        Assert.assertEquals(400, respCancelMap3.get("status"));
        requestReadData(respMap);


        logger.debug("@@@@@  CASE1-4 => 부분취소 [취소금액 : 6,600 / 부가가치세 : 700]");
        Map<String, Object> reqCancelMap4 = new HashMap<String, Object>(mapCase1Cancel4);
        reqCancelMap4.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap4 = requestCancel(reqCancelMap4);

        Assert.assertEquals(400, respCancelMap4.get("status"));
        requestReadData(respMap);


        logger.debug("@@@@@  CASE1-5 => 부분취소 [취소금액 : 6,600 / 부가가치세 : 600]");
        Map<String, Object> reqCancelMap5 = new HashMap<String, Object>(mapCase1Cancel5);
        reqCancelMap5.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap5 = requestCancel(reqCancelMap5);

        Assert.assertEquals(200, respCancelMap5.get("status"));
        Assert.assertEquals("P0000000000000000004", respCancelMap5.get("manageId"));
        Assert.assertNotNull(respCancelMap5.get("payStatement"));
        requestReadData(respMap);


        logger.debug("@@@@@  CASE1-6 => 부분취소 [취소금액 : 100 / 부가가치세 : null]");
        Map<String, Object> reqCancelMap6 = new HashMap<String, Object>(mapCase1Cancel6);
        reqCancelMap6.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap6 = requestCancel(reqCancelMap6);

        Assert.assertEquals(400, respCancelMap4.get("status"));
        requestReadData(respMap);


        requestPaymentList();
    }

    //@Test
    public void case2Test() throws Exception {

        logger.debug("@@@@@  CASE2 => 결제 [결제금액 : 20,000 / 부가가치세 : 909]");
        Map<String, Object> respMap = requestPayment(mapCase2Pay1);

        Assert.assertEquals(200, respMap.get("status"));
        Assert.assertEquals("P0000000000000000001", respMap.get("manageId"));
        Assert.assertNotNull(respMap.get("payStatement"));
        requestReadData(respMap);


        logger.debug("@@@@@  CASE2-1 => 부분취소 [취소금액 : 10,000 / 부가가치세 : 0]");
        Map<String, Object> reqCancelMap = new HashMap<String, Object>(mapCase2Cancel1);
        reqCancelMap.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap = requestCancel(reqCancelMap);

        Assert.assertEquals(200, respCancelMap.get("status"));
        Assert.assertEquals("P0000000000000000002", respCancelMap.get("manageId"));
        Assert.assertNotNull(respCancelMap.get("payStatement"));
        requestReadData(respMap);

        logger.debug("@@@@@  CASE2-2 => 부분취소 [취소금액 : 10,000 / 부가가치세 : 0]");
        Map<String, Object> reqCancelMap2 = new HashMap<String, Object>(mapCase2Cancel2);
        reqCancelMap2.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap2 = requestCancel(reqCancelMap2);

        Assert.assertEquals(400, respCancelMap2.get("status"));
        requestReadData(respMap);


        logger.debug("@@@@@  CASE2-3 => 부분취소 [취소금액 : 10,000 / 부가가치세 : 909]");
        Map<String, Object> reqCancelMap3 = new HashMap<String, Object>(mapCase2Cancel3);
        reqCancelMap3.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap3 = requestCancel(reqCancelMap3);

        Assert.assertEquals(200, respCancelMap.get("status"));
        Assert.assertEquals("P0000000000000000003", respCancelMap3.get("manageId"));
        Assert.assertNotNull(respCancelMap.get("payStatement"));
        requestReadData(respMap);

        requestPaymentList();
    }

    @Test
    public void case3Test() throws Exception {

        logger.debug("@@@@@  CASE3 => 결제 [결제금액 : 20,000 / 부가가치세 : null]");
        Map<String, Object> respMap = requestPayment(mapCase3Pay1);

        Assert.assertEquals(200, respMap.get("status"));
        Assert.assertEquals("P0000000000000000001", respMap.get("manageId"));
        Assert.assertNotNull(respMap.get("payStatement"));
        requestReadData(respMap);


        logger.debug("@@@@@  CASE3-1 => 부분취소 [취소금액 : 10,000 / 부가가치세 : 1,000]");
        Map<String, Object> reqCancelMap = new HashMap<String, Object>(mapCase3Cancel1);
        reqCancelMap.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap = requestCancel(reqCancelMap);

        Assert.assertEquals(200, respCancelMap.get("status"));
        Assert.assertEquals("P0000000000000000002", respCancelMap.get("manageId"));
        Assert.assertNotNull(respCancelMap.get("payStatement"));
        requestReadData(respMap);

        logger.debug("@@@@@  CASE3-2 => 부분취소 [취소금액 : 10,000 / 부가가치세 : 909]");
        Map<String, Object> reqCancelMap2 = new HashMap<String, Object>(mapCase3Cancel2);
        reqCancelMap2.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap2 = requestCancel(reqCancelMap2);

        Assert.assertEquals(400, respCancelMap2.get("status"));
        requestReadData(respMap);


        logger.debug("@@@@@  CASE3-3 => 부분취소 [취소금액 : 10,000 / 부가가치세 : null]");
        Map<String, Object> reqCancelMap3 = new HashMap<String, Object>(mapCase3Cancel3);
        reqCancelMap3.put("manageId", respMap.get("manageId"));
        Map<String, Object> respCancelMap3 = requestCancel(reqCancelMap3);

        Assert.assertEquals(200, respCancelMap.get("status"));
        Assert.assertEquals("P0000000000000000003", respCancelMap3.get("manageId"));
        Assert.assertNotNull(respCancelMap.get("payStatement"));
        requestReadData(respMap);

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
            "amount", 20000,
            "vat", 909
    );
    Map<String, Object> mapCase2Cancel1 = Map.of(
            "amount", 10000,
            "vat", 0
    );
    Map<String, Object> mapCase2Cancel2 = Map.of(
            "amount", 10000,
            "vat", 0
    );
    Map<String, Object> mapCase2Cancel3 = Map.of(
            "amount", 10000,
            "vat", 909
    );

    Map<String, Object> mapCase3Pay1 = Map.of(
            "cardNumber", "102030405060708",
            "expirationDate", "0528",
            "cvc", "705",
            "installment", 0,
            "amount", 20000
    );
    Map<String, Object> mapCase3Cancel1 = Map.of(
            "amount", 10000,
            "vat", 1000
    );
    Map<String, Object> mapCase3Cancel2 = Map.of(
            "amount", 10000,
            "vat", 909
    );
    Map<String, Object> mapCase3Cancel3 = Map.of(
            "amount", 10000
    );
}