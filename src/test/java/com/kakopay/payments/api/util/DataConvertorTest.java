package com.kakopay.payments.api.util;

import com.kakopay.payments.api.dto.CardInfo;
import com.kakopay.payments.api.security.AES256Cipher;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class DataConvertorTest {

    @Test
    void calVATTest() {
        int result = DataConvertor.calVAT(995);
        assertEquals(10,result);
    }

    @Test
    void encryptCardInfoTest(){

        CardInfo cardInfo = new CardInfo();
        cardInfo.setCardNumber("1234567812345678");
        cardInfo.setExpirationDate("1120");
        cardInfo.setCvc("987");

        String concatStr = DataConvertor.concatCardInfo(cardInfo);

        String secretKey = "12345678901234567890123456789012";
        String encStr = AES256Cipher.encryptCardInfo(concatStr, secretKey);

        String decStr = AES256Cipher.decryptCardInfo(encStr, secretKey);

        System.out.println("encStr = " + encStr);
        System.out.println("decStr = " + decStr);
    }


}