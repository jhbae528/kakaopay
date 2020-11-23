package com.kakaopay.payments.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDto {

    private String manageId;        // 관리번호

    private String payType;         // 결제 / 취소 구분

    private CardInfo cardInfo;      // 카드정보

    private AmountInfo amountInfo;  // 금액정보

    @Builder
    public RequestDto(String manageId, String payType, CardInfo cardInfo, AmountInfo amountInfo){
        this.manageId = manageId;
        this.payType = payType;
        this.cardInfo = cardInfo;
        this.amountInfo = amountInfo;
    }
}
