package com.kakaopay.payments.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class ResponseDto implements Serializable {

    private String manageId;        // 관리번호

    private CardInfo cardInfo;      // 카드정보

    private AmountInfo amountInfo;  // 금액정보

    private String payStatement;    // 결제 명세

    private String payType;         // 결제 / 취소 구분
    
    private Map<String, Object> optionalData;    // 추가 데이터

    @Builder
    public ResponseDto(String manageId, CardInfo cardInfo, AmountInfo amountInfo,
                       String payStatement, String payType, Map<String, Object> optionalData){
        this.manageId = manageId;
        this.cardInfo = cardInfo;
        this.amountInfo = amountInfo;
        this.payStatement = payStatement;
        this.payType = payType;
        this.optionalData = optionalData;
    }
}
