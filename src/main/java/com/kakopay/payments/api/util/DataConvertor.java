package com.kakopay.payments.api.util;

import com.kakopay.payments.api.dto.CardInfo;
import com.kakopay.payments.api.dto.RequestDto;
import com.kakopay.payments.api.security.AES256Cipher;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DataConvertor {

    public static int calVAT(int amount){
        double div = ((double)amount / 11);
        int round = (int)Math.round(div);
        return round;
    }

    // 카드정보를 암호화하기 위하여 문자합치기
    public static String concatCardInfo(CardInfo cardInfo) {
        return cardInfo.getCardNumber()
                + Constants.CARDINFO_SPLIT
                + cardInfo.getExpirationDate()
                + Constants.CARDINFO_SPLIT
                + cardInfo.getCvc();
    }

    // string 데이터 명세에 맞게 String 변경
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
