package com.kakaopay.payments.api.domain.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Data
@Table(name = "PAYMENT_INFO")
public class PaymentInfo {

    @Id
    @GenericGenerator(name="manage_id", strategy = "com.kakaopay.payments.api.domain.repository.GenerateManageId")
    @GeneratedValue(generator = "manage_id")
    @Column(name = "manage_id", length = 20)
    private String manageId;                // 거래관리번호

    @Column(name = "pay_type", length = 10)
    private String payType;                 // 거래 유형 (PAYMENT, CANCEL)

    @Column
    private int installment;                // 할부 (0: 일시불,

    @Column
    private int amount;                     // 거래금액

    @Column
    private int vat;                        // 부가가치세

    @Column(name = "pay_statement", length = 450)
    private String payStatement;            // 거래 명세

    @Column(name = "origin_manage_id")
    private String originManageId;          // 취소시 원거래관리번호


    @Builder
    public PaymentInfo(String manageId, String payType, int installment, int amount, int vat, String payStatement, String originManageId){
        this.manageId = manageId;
        this.payType = payType;
        this.installment = installment;
        this.amount = amount;
        this.vat = vat;
        this.payStatement = payStatement;
        this.originManageId = originManageId;
    }
}
