package com.kakopay.payments.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDto {

    private String manageId;        // 관리번호

    private CardInfo cardInfo;      // 카드정보

    private AmountInfo amountInfo;  // 금액정보
}
