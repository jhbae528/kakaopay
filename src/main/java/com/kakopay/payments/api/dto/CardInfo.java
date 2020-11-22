package com.kakopay.payments.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardInfo {

    String cardNumber;      // 카드번호 (10 ~ 16자리 숫자)

    String expirationDate;  // 유효기간 (4자리 숫자, mmyy)

    String cvc;             // cvc 번호 (3자리 숫자)
}
