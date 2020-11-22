package com.kakopay.payments.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {

    private String manageId;        // 관리번호

    private String payStatement;    // 결제 명세

    private CardInfo cardInfo;      // 카드정보

    private AmountInfo amountInfo;  // 금액정보

    private String payType;         // 결제 / 취소 구분
    
    private String optionalData;    // 추가 데이터
}
