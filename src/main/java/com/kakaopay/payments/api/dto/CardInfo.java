package com.kakaopay.payments.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@Getter
@Setter
public class CardInfo {

    String cardNumber;      // 카드번호 (10 ~ 16자리 숫자)

    String expirationDate;  // 유효기간 (4자리 숫자, mmyy)

    String cvc;             // cvc 번호 (3자리 숫자)

    @Builder
    public CardInfo(String cardNumber, String expirationDate, String cvc){
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cvc = cvc;
    }
}
