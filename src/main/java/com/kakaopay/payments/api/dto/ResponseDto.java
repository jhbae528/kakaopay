package com.kakaopay.payments.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {

    private String manageId;        // 관리번호

    private CardInfo cardInfo;      // 카드정보

    private AmountInfo amountInfo;  // 금액정보

    private String payStatement;    // 결제 명세

    private String payType;         // 결제 / 취소 구분
    
    private String optionalData;    // 추가 데이터

    @Builder
    public ResponseDto(String manageId, CardInfo cardInfo, AmountInfo amountInfo,
                       String payStatement, String payType, String optionalData){
        this.manageId = manageId;
        this.cardInfo = cardInfo;
        this.amountInfo = amountInfo;
        this.payStatement = payStatement;
        this.payType = payType;
        this.optionalData = optionalData;
    }
}
