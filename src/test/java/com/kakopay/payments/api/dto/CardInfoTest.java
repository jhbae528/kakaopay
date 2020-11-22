package com.kakopay.payments.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardInfoTest {

    @Test
    void getConcatenation() {
        CardInfo cardInfo = CardInfo.builder()
                .cardNumber("1234567812345678")
                .expirationDate("1120")
                .cvc("789")
                .build();
        String conStr = cardInfo.getConcatenation();
        assertEquals("1234567812345678|1120|789", conStr);
        System.out.println("concat string = " + conStr);
    }
}