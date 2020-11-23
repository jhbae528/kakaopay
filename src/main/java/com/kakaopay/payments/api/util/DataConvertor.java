package com.kakaopay.payments.api.util;

import com.kakaopay.payments.api.dto.CardInfo;

public class DataConvertor {

    /**
     * 부가가치세 계산
     * @param amount
     * @return
     */
    public static int calVAT(int amount){
        double div = ((double)amount / 11);
        int round = (int)Math.round(div);
        return round;
    }

    /**
     * 카드정보를 암호화하기 위하여 문자합치기
     * @param cardInfo
     * @return
     */
    public static String concatCardInfo(CardInfo cardInfo) {
        return cardInfo.getCardNumber()
                + Constants.CARDINFO_SPLIT
                + cardInfo.getExpirationDate()
                + Constants.CARDINFO_SPLIT
                + cardInfo.getCvc()
                + Constants.CARDINFO_SPLIT;
    }

    /**
     * 카드정보를 복호화하여 객체화 하기
     * @param str
     * @return
     */
    public static CardInfo objectCardInfo(String str) {

        int index1 = str.indexOf(Constants.CARDINFO_SPLIT);
        int index2 = str.indexOf(Constants.CARDINFO_SPLIT, index1+1);
        int index3 = str.indexOf(Constants.CARDINFO_SPLIT, index2+1);

        String cardNumber = str.substring(0, index1);
        String expDate = str.substring(index1+1, index2);
        String cvc = str.substring(index2+1, index3);

        CardInfo cardInfo = CardInfo.builder()
                .cardNumber(cardNumber)
                .expirationDate(expDate)
                .cvc(cvc)
                .build();
        return cardInfo;
    }

    /**
     * string 데이터 명세에 맞게 String 변경
     * @param str
     * @param length
     * @param type
     * @return
     */
    public static String formatPayStr(String str, int length, int type){
        if(str.length() >= length){
            return str;
        }
        char c = ' ';
        if(type == Constants.DATA_TYPE_NUM_ZERO){
            c = '0';
        }
        StringBuilder sb = new StringBuilder();
        if((type == Constants.DATA_TYPE_NUM_LEFT)
                || (type == Constants.DATA_TYPE_STRING)){
            sb.append(str);
        }
        int size = length - str.length();
        while (size > 0) {
            sb.append(c);
            size--;
        }
        if((type == Constants.DATA_TYPE_NUM)
                || (type == Constants.DATA_TYPE_NUM_ZERO)){
            sb.append(str);
        }
        return sb.toString();
    }



}
