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


    }
}