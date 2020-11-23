package com.kakaopay.payments.api.util;

public class Constants {

    public static final String CARDINFO_SPLIT = "|";    // 구분자

    public static final String PAY_APPROVE = "PAYMENT"; // 결제
    public static final String PAY_CANCEL = "CANCEL";   // 취소

    public static final String CANCEL_ALL = "ALL";      // 전체 취소
    public static final String CANCEL_PART = "PART";    // 부분 취소

    public static final int DATA_TYPE_NUM = 0;          // 숫자
    public static final int DATA_TYPE_NUM_ZERO = 1;     // 숫자
    public static final int DATA_TYPE_NUM_LEFT = 2;     // 숫자
    public static final int DATA_TYPE_STRING = 3;       // 숫자


    public class PayStatementSize{  // string 데이터 명세

        // 공통헤더부문
        public static final int DATA_SIZE = 4;           // 데이터 길이
        public static final int PAY_TYPE = 10;         // 데이터 구분
        public static final int MANAGE_ID = 20;         // 관리번호

        // 데이터부문
        public static final int CARD_NUMBER = 20;       // 카드번호
        public static final int INSTALLMENT = 2;        // 할부
        public static final int EXP_DATE = 4;           // 유효기간
        public static final int CVC = 3;                // CVC 번호
        public static final int AMOUNT = 10;            // 거래금액
        public static final int VAT = 10;               // 부가가치세
        public static final int ORIGIN_MNG_ID = 20;     // 원거래관리번호
        public static final int ENC_CARD_INFO = 300;    // 암호화된 카드정보
        public static final int RESERVED = 47;          // 예비필드

        public static final int TOT_SIZE = DATA_SIZE + PAY_TYPE + MANAGE_ID + CARD_NUMBER + INSTALLMENT + EXP_DATE + CVC + AMOUNT + VAT + ORIGIN_MNG_ID + ENC_CARD_INFO + RESERVED;
    }

}
