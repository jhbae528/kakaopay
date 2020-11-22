package com.kakopay.payments.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmountInfo {

    int installment;    // 할부

    int amount; // 결제금액

    int vat;    // 부가가치세

    AmountInfo(int installment, int amount, int vat){
        this.installment = installment;
        this.amount = amount;
        this.vat = vat;
    }
}
