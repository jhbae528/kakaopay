package com.kakaopay.payments.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmountInfo {

    private int installment;    // 할부

    private int amount;         // 결제금액

    private Integer vat;        // 부가가치세

    @Builder
    public AmountInfo(int installment, int amount, Integer vat){
        this.installment = installment;
        this.amount = amount;
        this.vat = vat;
    }
}
