package com.kakopay.payments.api.domain.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Data
public class CancelInfo {

    @Id
    @GenericGenerator(name="cancel_id", strategy = "com.kakopay.payments.api.domain.repository.GenerateCancelId")
    @GeneratedValue(generator = "cancel_id")
    @Column(name = "cancel_id", length = 20)
    private String cancelId;            // 취소관리번호

    @Column(name = "cancel_type", length = 10)
    private String cancelType;          // 취소 유형

    @Column(name = "cancel_amount")
    private int cancelAmount;           // 취소금액

    @Column(name = "cancel_vat")
    private int cancelVat;              // 취소 부가가치세

    @Column(name="manage_id")
    private String manageId;            // 결제관리번호
}
