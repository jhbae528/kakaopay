package com.kakopay.payments.api.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Data
public class PaymentInfo {

    @Id
    @GenericGenerator(name="manage_id", strategy = "com.kakopay.payments.api.domain.repository.GenerateManageId")
    @GeneratedValue(generator = "manage_id")
    @Column(name = "manage_id", length = 20)
    private String manageId;                // 거래관리번호

    @Column
    private int installment;                // 할부 (0: 일시불,

    @Column(name = "pay_type", length = 10)
    private String payType;                 // 거래 유형 (PAYMENT, CANCEL)

    @Column(name = "pay_statement", length = 450)
    private String payStatement;            // 거래 명세

    @Column
    private int amount;                     // 거래금액

    @Column
    private int vat;                        // 부가가치세

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "manage_id")
    private List<CancelInfo> cancelList;    // 취소거래 FK

    public void addCancelInfo(CancelInfo cancelInfo){
        if(cancelList == null){
            cancelList = new ArrayList<CancelInfo>();
        }
        cancelList.add(cancelInfo);
    }
}
