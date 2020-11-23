package com.kakaopay.payments.api.service;

import com.kakaopay.payments.api.domain.entity.PaymentInfo;
import com.kakaopay.payments.api.domain.repository.PaymentInfoRepository;
import com.kakaopay.payments.api.dto.AmountInfo;
import com.kakaopay.payments.api.dto.CardInfo;
import com.kakaopay.payments.api.dto.RequestDto;
import com.kakaopay.payments.api.dto.ResponseDto;
import com.kakaopay.payments.api.security.AES256Cipher;
import com.kakaopay.payments.api.util.Constants;
import com.kakaopay.payments.api.util.DataConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService{

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    PaymentInfoRepository paymentInfoRepository;

    private String secretKey = "12345678901234567890123456789012";  // TODO : DataBase 조회 후 derivation

    @Override
    public ResponseDto doPayment(RequestDto requestDto) {

        checkVCT(requestDto.getAmountInfo());

        PaymentInfo paymentInfo = PaymentInfo.builder()
                .payType(requestDto.getPayType())
                .installment(requestDto.getAmountInfo().getInstallment())
                .amount(requestDto.getAmountInfo().getAmount())
                .vat(requestDto.getAmountInfo().getVat())
                .build();

        PaymentInfo savedPaymentInfo = paymentInfoRepository.save(paymentInfo);  // 결제 정보 저장
        logger.debug("###  savedPaymentInfo =   savedInfo = " + savedPaymentInfo.toString());

        requestDto.setManageId(savedPaymentInfo.getManageId());
        String payStatementStr = generatePayStatement(requestDto, secretKey);   // string 명세 생성

        savedPaymentInfo.setPayStatement(payStatementStr);
        savedPaymentInfo = paymentInfoRepository.save(savedPaymentInfo);   // string 명세 저장
        logger.debug("###  updatedPaymentInfo = " + savedPaymentInfo.toString());

        ResponseDto responseDto = ResponseDto.builder()
                .manageId(savedPaymentInfo.getManageId())
                .payStatement(savedPaymentInfo.getPayStatement())
                .build();
        return responseDto;
    }

    @Override
    public ResponseDto doCancel(RequestDto requestDto) {

        AmountInfo amountInfo = requestDto.getAmountInfo();
        amountInfo.setInstallment(0);
        checkVCT(amountInfo);

        Optional<PaymentInfo> optionalPaymentInfo = paymentInfoRepository.findById(requestDto.getManageId());
        if(optionalPaymentInfo.isPresent()){
            PaymentInfo paymentInfo = optionalPaymentInfo.get();

            String payStatement = paymentInfo.getPayStatement();    // string 명세
            int encOffset = Constants.PayStatementSize.TOT_SIZE - Constants.PayStatementSize.ENC_CARD_INFO - Constants.PayStatementSize.RESERVED;   // 암호화된 카드정보 위치
            String encCardInfo = payStatement.substring(encOffset, Constants.PayStatementSize.ENC_CARD_INFO);   // 암호화된 정보 추출
            int indexBlank = encCardInfo.indexOf(' ');
            encCardInfo = encCardInfo.substring(0, indexBlank);
            logger.debug("encCardInfo = " + encCardInfo);

            String decData = AES256Cipher.decryptCardInfo(encCardInfo, secretKey);
            logger.debug("decData = " + decData);

            CardInfo cardInfo = DataConvertor.getCardInfo(decData);
            requestDto.setCardInfo(cardInfo);

            PaymentInfo cancelPaymentsInfo = PaymentInfo.builder()
                    .payType(requestDto.getPayType())
                    .installment(requestDto.getAmountInfo().getInstallment())
                    .amount(requestDto.getAmountInfo().getAmount())
                    .vat(requestDto.getAmountInfo().getVat())
                    .originManageId(requestDto.getManageId())
                    .build();

            PaymentInfo savedPaymentInfo = paymentInfoRepository.save(cancelPaymentsInfo);  // 취소 정보 저장
            logger.debug("###  savedPaymentInfo =   savedInfo = " + savedPaymentInfo.toString());

            requestDto.setManageId(savedPaymentInfo.getManageId());
            String payStatementStr = generatePayStatement(requestDto, secretKey);   // string 명세 생성

            savedPaymentInfo.setPayStatement(payStatementStr);
            savedPaymentInfo = paymentInfoRepository.save(savedPaymentInfo);   // string 명세 저장
            logger.debug("###  updatedPaymentInfo = " + savedPaymentInfo.toString());

            PaymentInfo cancelPaymentsInfo2 = PaymentInfo.builder()
                    .payType(requestDto.getPayType())
                    .installment(requestDto.getAmountInfo().getInstallment())
                    .amount(requestDto.getAmountInfo().getAmount())
                    .vat(requestDto.getAmountInfo().getVat())
                    .originManageId(paymentInfo.getManageId())
                    .build();

            PaymentInfo savedPaymentInfo2 = paymentInfoRepository.save(cancelPaymentsInfo2);  // 취소 정보 저장
            logger.debug("###  savedPaymentInfo2 =   savedInfo = " + savedPaymentInfo2.toString());

            requestDto.setManageId(savedPaymentInfo2.getManageId());
            String payStatementStr2 = generatePayStatement(requestDto, secretKey);   // string 명세 생성

            savedPaymentInfo2.setPayStatement(payStatementStr2);
            savedPaymentInfo2 = paymentInfoRepository.save(savedPaymentInfo2);   // string 명세 저장
            logger.debug("###  updatedPaymentInfo2 = " + savedPaymentInfo2.toString());

            List<PaymentInfo> paymentInfos = paymentInfoRepository.findByOriginManageId(paymentInfo.getManageId());

            ResponseDto responseDto = ResponseDto.builder()
                    .manageId(savedPaymentInfo.getManageId())
                    .payStatement(savedPaymentInfo.getPayStatement())
                    .build();
            return responseDto;
        }else{
            // TODO exception 해당하는 manageId가 존재하지 않음
        }
        return null;
    }

    @Override
    public ResponseDto doReadData(String manageId) {

        return null;
    }

    @Override
    public List<PaymentInfo> doPaymentList() {
        return paymentInfoRepository.findAll();
    }

    private void checkVCT(AmountInfo amountInfo){
        if(amountInfo.getVat() == null)
            amountInfo.setVat(DataConvertor.calVAT(amountInfo.getAmount()));
    }

    // 결제 string 데이터 명세 생성
    public String generatePayStatement(RequestDto requestDto, String secretKey){

        String concatCardInfo = DataConvertor.concatCardInfo(requestDto.getCardInfo());   // 카드정보 문자열 합치기
        String encData = AES256Cipher.encryptCardInfo(concatCardInfo, secretKey);

        String payType = DataConvertor.formatPayStr(requestDto.getPayType(), Constants.PayStatementSize.PAY_TYPE, Constants.DATA_TYPE_STRING);
        String manageId = DataConvertor.formatPayStr(requestDto.getManageId(), Constants.PayStatementSize.MANAGE_ID, Constants.DATA_TYPE_STRING);
        String cardNum = DataConvertor.formatPayStr(requestDto.getCardInfo().getCardNumber(), Constants.PayStatementSize.CARD_NUMBER, Constants.DATA_TYPE_NUM_LEFT);
        String installment = DataConvertor.formatPayStr(String.valueOf(requestDto.getAmountInfo().getInstallment()), Constants.PayStatementSize.INSTALLMENT, Constants.DATA_TYPE_NUM_ZERO);
        String expDate = DataConvertor.formatPayStr(requestDto.getCardInfo().getExpirationDate(), Constants.PayStatementSize.EXP_DATE, Constants.DATA_TYPE_NUM_LEFT);
        String cvc = DataConvertor.formatPayStr(requestDto.getCardInfo().getCvc(), Constants.PayStatementSize.CVC, Constants.DATA_TYPE_NUM_LEFT);
        String amount = DataConvertor.formatPayStr(String.valueOf(requestDto.getAmountInfo().getAmount()), Constants.PayStatementSize.AMOUNT, Constants.DATA_TYPE_NUM);
        String vat = DataConvertor.formatPayStr(String.valueOf(requestDto.getAmountInfo().getVat().intValue()), Constants.PayStatementSize.VAT, Constants.DATA_TYPE_NUM_ZERO);
        String originManageId = DataConvertor.formatPayStr("", Constants.PayStatementSize.ORIGIN_MNG_ID, Constants.DATA_TYPE_STRING);
        String encCardInfo = DataConvertor.formatPayStr(encData, Constants.PayStatementSize.ENC_CARD_INFO, Constants.DATA_TYPE_STRING);
        String reserved = DataConvertor.formatPayStr("", Constants.PayStatementSize.RESERVED, Constants.DATA_TYPE_STRING);

        StringBuilder sb = new StringBuilder();
        sb.append(payType).append(manageId).append(cardNum).append(installment).append(expDate).append(cvc)
                .append(amount).append(vat).append(originManageId).append(encCardInfo).append(reserved);
        String concatStr = sb.toString();
        String dataSize = DataConvertor.formatPayStr(String.valueOf(concatStr.length()), Constants.PayStatementSize.DATA_SIZE, Constants.DATA_TYPE_NUM);
        return dataSize + concatStr;
    }
}
