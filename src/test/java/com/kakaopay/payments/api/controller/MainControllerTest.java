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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class MainControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(MainControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    /**
     * 3. 필수 구현 API 기능
     * 1. 결제 API
     * @throws Exception
     */
    @Test
    public void reqPay() throws Exception {

        Map<String, Object> respMap = requestPayment(mapCase1Pay1);
        logger.debug(respMap.toString());

        Assert.assertEquals(200, respMap.get("status"));
        Assert.assertEquals("P0000000000000000001", respMap.get("manageId"));
        Assert.assertNotNull(respMap.get("payStatement"));
    }

    /**
     * 3. 필수 구현 API 기능
     * 2. 결제 취소 API
     * @throws Exception
     */
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

    /**
     * 3. 필수 구현 API 기능
     * 3. 데이터 조회 API
     * @throws Exception
     */
    @Test
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

    /**
     * 4.선택문제
     * Test Case 1
     * @throws Exception
     */
    @Test
    public void case1Test() throws Exception {
        case1();
        requestPaymentList();
    }

    /**
     * 4.선택문제
     * Test Case 2
     * @throws Exception
     */
    @Test
    public void case2Test() throws Exception {
        case2();
        requestPaymentList();
    }

    /**
     * 4.선택문제
     * Test Case 3
     * @throws Exception
     */
    @Test
    public void case3Test() throws Exception {
        case3();
        requestPaymentList();
    }

    /**
     * 4.선택문제
     * Multi Thread - 결제 - 하나의 카드번호로 동시에 결제를 할 수 없습니다.
     * @throws Exception
     */
    @Test
    public void multiThreadCasePaymentTest() throws Exception{

        ExecutorService exeService = Executors.newFixedThreadPool(3);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    case1();
                } catch (Exception e) {}
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                try {
                    case2();
                } catch (Exception e) {}
            }
        };
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                try {
                    case3();
                } catch (Exception e) {}
            }
        };
        exeService.execute(runnable);
        exeService.execute(runnable2);
        exeService.execute(runnable3);
        try{
            Thread.sleep(3000);
        }catch(InterruptedException e){}

        exeService.shutdown();
    }

    /**
     * 4.선택문제
     * Multi Thread
     * - 전체취소 - 결제 한 건에 대한 전체취소를 동시에 할 수 없습니다.
     * @throws Exception
     */
    @Test
    public void multiThreadCaseCancelAllTest() throws Exception{

        ExecutorService exeService = Executors.newFixedThreadPool(10);

        logger.debug("@@@@@  CASE1 => 결제 [결제금액 : 11,000 / 부가가치세 : 1,000] - 성공");
        String manageId = casePayment(mapCase1Pay1);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    case1CancelAll(manageId, false);
                } catch (Exception e) {}
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                try {
                    case1CancelAll(manageId, false);
                } catch (Exception e) {}
            }
        };
        exeService.execute(runnable);
        exeService.execute(runnable2);
        try{
            Thread.sleep(3000);
        }catch(InterruptedException e){}

        exeService.shutdown();
    }
    
    /**
     * 4.선택문제
     * Multi Thread
     * - 부분취소 - 결제 한 건에 대한 부분취소를 동시에 할 수 없습니다.
     * @throws Exception
     */
    @Test
    public void multiThreadCaseCancelTest() throws Exception{

        ExecutorService exeService = Executors.newFixedThreadPool(10);

        logger.debug("@@@@@  CASE1 => 결제 [결제금액 : 11,000 / 부가가치세 : 1,000] - 성공");
        String manageId = casePayment(mapCase1Pay1);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    case1Cancel1(manageId, false);
                } catch (Exception e) {}
            }
        };
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                try {
                    case1Cancel2(manageId, false);
                } catch (Exception e) {}
            }
        };
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                try {
                    case1Cancel3(manageId, false);
                } catch (Exception e) {}
            }
        };
        Runnable runnable4 = new Runnable() {
            @Override
            public void run() {
                try {
                    case1Cancel4(manageId, false);
                } catch (Exception e) {}
            }
        };
        Runnable runnable5 = new Runnable() {
            @Override
            public void run() {
                try {
                    case1Cancel5(manageId, false);
                } catch (Exception e) {}
            }
        };
        Runnable runnable6 = new Runnable() {
            @Override
            public void run() {
                try {
                    case1Cancel6(manageId, false);
                } catch (Exception e) {}
            }
        };
        exeService.execute(runnable);
        exeService.execute(runnable2);
        exeService.execute(runnable3);
        exeService.execute(runnable4);
        exeService.execute(runnable5);
        exeService.execute(runnable6);
        try{
            Thread.sleep(3000);
        }catch(InterruptedException e){}

        exeService.shutdown();
    }



    private void case1Cancel1(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE1-1 => 부분취소 [취소금액 : 1,100 / 부가가치세 : 100] - 성공");
        Map<String, Object> respCancelMap = caseCancel(mapCase1Cancel1, manageId);
        if(assertion){
            Assert.assertEquals(200, respCancelMap.get("status"));
            Assert.assertNotNull(respCancelMap.get("payStatement"));
        }
    }
    private void case1Cancel2(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE1-2 => 부분취소 [취소금액 : 3,300 / 부가가치세 : null] - 성공");
        Map<String, Object> respCancelMap2 = caseCancel(mapCase1Cancel2, manageId);
        if(assertion){
            Assert.assertEquals(200, respCancelMap2.get("status"));
            Assert.assertNotNull(respCancelMap2.get("payStatement"));
        }
    }
    private void case1Cancel3(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE1-3 => 부분취소 [취소금액 : 7,000 / 부가가치세 : null] - 실패");
        Map<String, Object> respCancelMap3 = caseCancel(mapCase1Cancel3, manageId);
        if(assertion){
            Assert.assertEquals(400, respCancelMap3.get("status"));
        }
    }
    private void case1Cancel4(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE1-4 => 부분취소 [취소금액 : 6,600 / 부가가치세 : 700] - 실패");
        Map<String, Object> respCancelMap4 = caseCancel(mapCase1Cancel4, manageId);
        if(assertion){
            Assert.assertEquals(400, respCancelMap4.get("status"));
        }
    }
    private void case1Cancel5(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE1-5 => 부분취소 [취소금액 : 6,600 / 부가가치세 : 600] - 성공");
        Map<String, Object> respCancelMap5 = caseCancel(mapCase1Cancel5, manageId);
        if(assertion){
            Assert.assertEquals(200, respCancelMap5.get("status"));
            Assert.assertNotNull(respCancelMap5.get("payStatement"));
        }
    }
    private void case1Cancel6(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE1-6 => 부분취소 [취소금액 : 100 / 부가가치세 : null] - 실패");
        Map<String, Object> respCancelMap6 = caseCancel(mapCase1Cancel6, manageId);
        if(assertion){
            Assert.assertEquals(400, respCancelMap6.get("status"));
        }
    }
    private void case1CancelAll(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE1-All => 전체취소 [취소금액 : 11,000 / 부가가치세 : 1,000] - 성공");
        Map<String, Object> respCancelMap6 = caseCancel(mapCase1CancelALL, manageId);
        if(assertion){
            Assert.assertEquals(400, respCancelMap6.get("status"));
        }
    }

    private void case1() throws Exception {
        logger.debug("@@@@@  CASE1 => 결제 [결제금액 : 11,000 / 부가가치세 : 1,000] - 성공");
        String manageId = casePayment(mapCase1Pay1);
        case1Cancel1(manageId, true);
        case1Cancel2(manageId, true);
        case1Cancel3(manageId, true);
        case1Cancel4(manageId, true);
        case1Cancel5(manageId, true);
        case1Cancel6(manageId, true);
    }

    private void case2Cancel1(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE2-1 => 부분취소 [취소금액 : 10,000 / 부가가치세 : 0] - 성공");
        Map<String, Object> respCancelMap = caseCancel(mapCase2Cancel1, manageId);
        if(assertion){
            Assert.assertEquals(200, respCancelMap.get("status"));
            Assert.assertNotNull(respCancelMap.get("payStatement"));
        }
    }
    private void case2Cancel2(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE2-2 => 부분취소 [취소금액 : 10,000 / 부가가치세 : 0] - 실패");
        Map<String, Object> respCancelMap2 = caseCancel(mapCase2Cancel2, manageId);
        if(assertion){
            Assert.assertEquals(400, respCancelMap2.get("status"));
        }
    }
    private void case2Cancel3(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE2-3 => 부분취소 [취소금액 : 10,000 / 부가가치세 : 909] - 성공");
        Map<String, Object> respCancelMap3 = caseCancel(mapCase2Cancel3, manageId);
        if(assertion){
            Assert.assertEquals(200, respCancelMap3.get("status"));
            Assert.assertNotNull(respCancelMap3.get("payStatement"));
        }
    }

    private void case2() throws Exception {
        logger.debug("@@@@@  CASE2 => 결제 [결제금액 : 20,000 / 부가가치세 : 909] - 성공");
        String manageId = casePayment(mapCase2Pay1);
        case2Cancel1(manageId, true);
        case2Cancel2(manageId, true);
        case2Cancel3(manageId, true);
    }

    private void case3Cancel1(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE3-1 => 부분취소 [취소금액 : 10,000 / 부가가치세 : 1,000] - 성공");
        Map<String, Object> respCancelMap = caseCancel(mapCase3Cancel1, manageId);
        if(assertion){
            Assert.assertEquals(200, respCancelMap.get("status"));
            Assert.assertNotNull(respCancelMap.get("payStatement"));
        }
    }
    private void case3Cancel2(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE3-2 => 부분취소 [취소금액 : 10,000 / 부가가치세 : 909] - 실패");
        Map<String, Object> respCancelMap2 = caseCancel(mapCase3Cancel2, manageId);
        if(assertion){
            Assert.assertEquals(400, respCancelMap2.get("status"));
        }
    }
    private void case3Cancel3(String manageId, boolean assertion) throws Exception{
        logger.debug("@@@@@  CASE3-3 => 부분취소 [취소금액 : 10,000 / 부가가치세 : null] - 성공");
        Map<String, Object> respCancelMap3 = caseCancel(mapCase3Cancel3, manageId);
        if(assertion){
            Assert.assertEquals(200, respCancelMap3.get("status"));
            Assert.assertNotNull(respCancelMap3.get("payStatement"));
        }
    }

    private void case3() throws Exception {
        logger.debug("@@@@@  CASE3 => 결제 [결제금액 : 20,000 / 부가가치세 : null] - 성공");
        String manageId = casePayment(mapCase3Pay1);
        case3Cancel1(manageId, true);
        case3Cancel2(manageId, true);
        case3Cancel3(manageId, true);
    }

    private String casePayment(Map<String, Object> map) throws Exception{
        Map<String, Object> respMap = requestPayment(map);

        Assert.assertEquals(200, respMap.get("status"));
        Assert.assertNotNull(respMap.get("payStatement"));

        Map<String, Object> readMap = Map.of("manageId", respMap.get("manageId"));
        requestReadData(readMap);
        return (String)respMap.get("manageId");
    }

    private Map<String, Object> caseCancel(Map<String, Object> map, String manageId) throws Exception{
        Map<String, Object> reqCancelMap = new HashMap<String, Object>(map);
        reqCancelMap.put("manageId", manageId);
        Map<String, Object> respCancelMap = requestCancel(reqCancelMap);

        if((int)respCancelMap.get("status") == 200){
            Map<String, Object> readMap = Map.of("manageId", respCancelMap.get("manageId"));
            requestReadData(readMap);
        }
        return respCancelMap;
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
            "cardNumber", "371002300491824",
            "expirationDate", "0324",
            "cvc", "805",
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
    Map<String, Object> mapCase1CancelALL = Map.of(
            "amount", 11000,
            "vat", 1000
    );

    Map<String, Object> mapCase2Pay1 = Map.of(
            "cardNumber", "371002300491824",
            "expirationDate", "0324",
            "cvc", "805",
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
            "cardNumber", "371002300491824",
            "expirationDate", "0324",
            "cvc", "805",
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