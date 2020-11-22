package com.kakopay.payments.api.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class PaymentsInfoTest {

    @Test
    void getManageId() {
        PaymentsInfo paymentsInfo = PaymentsInfo.builder()
                .installment(0)
                .payType("PAYMENT")
                .payStatement("abcde")
                .amount(1000)
                .vat(100)
                .build();

        assertEquals("PAYMENT", paymentsInfo.getPayType());
        assertEquals("abcde", paymentsInfo.getPayStatement());
        assertEquals(1000, paymentsInfo.getAmount());
        assertEquals(100, paymentsInfo.getVat());
    }
}