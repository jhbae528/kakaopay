package com.kakaopay.payments.api.util;

import com.kakaopay.payments.api.security.AES256Cipher;
import com.kakaopay.payments.api.dto.CardInfo;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class DataConvertorTest {

    @Test
    void calVATTest() {
        int result = DataConvertor.calVAT(995);
        assertEquals(10,result);
    }

    @Test
    void encryptCardInfoTest() throws Exception{

        CardInfo cardInfo = CardInfo.builder()
                .cardNumber("1234567812345678")
                .expirationDate("1120")
                .cvc("987")
                .build();

        String concatStr = DataConvertor.concatCardInfo(cardInfo);

        String secretKey = "12345678901234567890123456789012";
        String encStr = AES256Cipher.encryptCardInfo(concatStr, secretKey);

        String decStr = AES256Cipher.decryptCardInfo(encStr, secretKey);

        System.out.println("encStr = " + encStr);
        System.out.println("decStr = " + decStr);
    }


}