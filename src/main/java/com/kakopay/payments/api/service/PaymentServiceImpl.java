package com.kakopay.payments.api.service;

import com.kakopay.payments.api.domain.repository.PaymentsInfoRepository;
import com.kakopay.payments.api.dto.RequestDto;
import com.kakopay.payments.api.dto.ResponseDto;
import com.kakopay.payments.api.security.AES256Cipher;
import com.kakopay.payments.api.util.Constants;
import com.kakopay.payments.api.util.DataConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.kakopay.payments.api.util.DataConvertor.calVAT;

@Service
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    PaymentsInfoRepository paymentsInfoRepository;

    private String secretKey = "12345678901234567890123456789012";  // TODO : DataBase 조회 후 derivation

    @Override
    public ResponseDto doPayment(RequestDto requestDto) {

        String payStatementStr = generatePayStatement(requestDto, secretKey);

        int a = Constants.PayStatementSize.DATA_LEN;
        return null;
    }

    @Override
    public ResponseDto doCancel(RequestDto requestDto) {

        return null;
    }

    @Override
    public ResponseDto doReadData(String manageId) {

        return null;
    }

    // 결제 string 데이터 명세 생성
    public String generatePayStatement(RequestDto requestDto, String secretKey){

        String concatCardInfo = DataConvertor.concatCardInfo(requestDto.getCardInfo());   // 카드정보 문자열 합치기
        String encCardInfo = AES256Cipher.encryptCardInfo(concatCardInfo, secretKey);

        String cardNum = requestDto.getCardInfo().getCardNumber();
        String expirDate = requestDto.getCardInfo().getExpirationDate();
        String cvc = requestDto.getCardInfo().getCvc();
        String installment = String.valueOf(requestDto.getAmountInfo().getInstallment());
        String amount = String.valueOf(requestDto.getAmountInfo().getAmount());

        int vat;
        if(requestDto.getAmountInfo().getVat() == null)
            vat = calVAT(requestDto.getAmountInfo().getAmount());
        else
            vat = requestDto.getAmountInfo().getVat().intValue();

        int totalSize = 0;
        String dataType = DataConvertor.formatPayStr(requestDto.getPayType(), Constants.PayStatementSize.DATA_TYPE, Constants.DATA_TYPE_STRING);

        return null;
    }
}
