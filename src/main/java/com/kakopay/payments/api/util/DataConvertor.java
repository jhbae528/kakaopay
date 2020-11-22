package com.kakopay.payments.api.util;

import com.kakopay.payments.api.dto.CardInfo;

public class DataConvertor {

    // 카드정보를 암호화하기 위하여 문자합치기
    public String concatCardInfo(CardInfo cardInfo) {
        return cardInfo.getCardNumber()
                + Constants.CARDINFO_SPLIT
                + cardInfo.getExpirationDate()
                + Constants.CARDINFO_SPLIT
                + cardInfo.getCvc();
    }
}
