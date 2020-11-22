package com.kakopay.payments.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmountInfo {

    private int installment;    // 할부

    private int amount;         // 결제금액

    private Integer vat;        // 부가가치세
}
