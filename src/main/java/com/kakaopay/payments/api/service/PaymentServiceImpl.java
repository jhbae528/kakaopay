package com.kakaopay.payments.api.service;

import com.kakaopay.payments.api.domain.entity.PaymentInfo;
import com.kakaopay.payments.api.domain.repository.PaymentInfoRepository;
import com.kakaopay.payments.api.dto.AmountInfo;
import com.kakaopay.payments.api.dto.CardInfo;
import com.kakaopay.payments.api.dto.RequestDto;
import com.kakaopay.payments.api.dto.ResponseDto;
import com.kakaopay.payments.api.exception.InvalidDataException;
import com.kakaopay.payments.api.security.AES256Cipher;
import com.kakaopay.payments.api.util.Constants;
import com.kakaopay.payments.api.util.DataConvertor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    PaymentInfoRepository paymentInfoRepository;

    private String secretKey = "12345678901234567890123456789012";  // TODO : DataBase 조회 후 derivation


    @Override
    public ResponseDto processPayment(RequestDto requestDto) throws Exception{

        if(!validationVAT(requestDto.getAmountInfo()))    // 부가가치세 검증
            throw new InvalidDataException();

        PaymentInfo paymentInfo = PaymentInfo.builder()
                .payType(requestDto.getPayType())
                .installment(requestDto.getAmountInfo().getInstallment())
                .amount(requestDto.getAmountInfo().getAmount())
                .vat(requestDto.getAmountInfo().getVat())
                .build();

        savePaymentInfo(requestDto, paymentInfo);   // 결제정보 저장

        ResponseDto responseDto = ResponseDto.builder()
                .manageId(paymentInfo.getManageId())
                .payStatement(paymentInfo.getPayStatement())
                .build();
        return responseDto;
    }

    @Override
    public ResponseDto processCancel(RequestDto requestDto) throws Exception{

        AmountInfo amountInfo = requestDto.getAmountInfo();
        amountInfo.setInstallment(0);   // 취소거래시 할부 =: 0

        Optional<PaymentInfo> opPaymentInfo = paymentInfoRepository.findById(requestDto.getManageId());

        if(!opPaymentInfo.isPresent())      // manageId 존재하는지 검증
            throw new InvalidDataException();

        PaymentInfo paymentInfo = opPaymentInfo.get();

        if(!validationCancelVAT(paymentInfo, amountInfo))    // 부가가치세 검증
            throw new InvalidDataException();

        if(!validationCancelData(paymentInfo, amountInfo))  // 원 거래 금액 및 부가세와 취소 금액의 및 부가세와의 검증
            throw new InvalidDataException();

        requestDto.setCardInfo(extractCardInfo(paymentInfo.getPayStatement()));    // string 명세서에서 cardInfo 추출

        PaymentInfo cancelPaymentInfo = PaymentInfo.builder()
                .payType(requestDto.getPayType())
                .installment(requestDto.getAmountInfo().getInstallment())
                .amount(requestDto.getAmountInfo().getAmount())
                .vat(requestDto.getAmountInfo().getVat())
                .originManageId(requestDto.getManageId())
                .build();

        savePaymentInfo(requestDto, cancelPaymentInfo);   // 취소정보 저장

        reduceOriginPaymentAmount(paymentInfo, cancelPaymentInfo);  // 원 결제 정보를 취소금액만큼 차감

        paymentInfoRepository.save(paymentInfo);  // 결제 금액정보 다시 저장

        ResponseDto responseDto = ResponseDto.builder()
                .manageId(cancelPaymentInfo.getManageId())
                .payStatement(cancelPaymentInfo.getPayStatement())
                .build();
        return responseDto;
    }

    @Override
    public ResponseDto processReadPayment(String manageId) throws Exception{

        Optional<PaymentInfo> opPaymentInfo = paymentInfoRepository.findById(manageId);

        if(!opPaymentInfo.isPresent())  // manageId 존재하는지 검증
            throw new InvalidDataException();

        PaymentInfo paymentInfo = opPaymentInfo.get();
        CardInfo cardInfo = extractCardInfo(paymentInfo.getPayStatement()); // 원거래로부터 카드정보 추출
        
        maskingCardInfo(cardInfo);  // 카드정보 마스킹

        AmountInfo amountInfo = AmountInfo.builder()
                .amount(paymentInfo.getAmount())
                .vat(paymentInfo.getVat())
                .build();

        Map<String, Object> optionalMap = new HashMap<String, Object>();    // 응답값에 옵션 정보 추가 (할부)
        optionalMap.put("installment", amountInfo.getInstallment());

        ResponseDto responseDto = ResponseDto.builder()
                .manageId(paymentInfo.getManageId())
                .cardInfo(cardInfo)
                .payType(paymentInfo.getPayType())
                .amountInfo(amountInfo)
                .optionalData(optionalMap)
                .build();
        return responseDto;
    }

    @Override
    public List<PaymentInfo> processReadPaymentList() throws Exception {
        return paymentInfoRepository.findAll();
    }

    /**
     * 결제 및 취소 정보 저장
     * @param requestDto
     * @param paymentInfo
     * @throws Exception
     */
    private void savePaymentInfo(RequestDto requestDto, PaymentInfo paymentInfo) throws Exception{

        paymentInfoRepository.save(paymentInfo);  // 결제 or 취소 정보 저장
        requestDto.setManageId(paymentInfo.getManageId());
        paymentInfo.setPayStatement(generatePayStatement(requestDto, secretKey));   // string 명세 생성
        paymentInfoRepository.save(paymentInfo);   // string 명세 저장

        log.debug("###  updatedPaymentInfo = " + paymentInfo.toString());
    }

    /**
     * 원 결제정보에서 취소금액 및 부가가치세만큼 차감
     * @param paymentInfo   원 결제정보
     * @param cancelPaymentInfo   차감하고자 하는 금액 및 부가가치세 정보
     */
    private void reduceOriginPaymentAmount(PaymentInfo paymentInfo, PaymentInfo cancelPaymentInfo){
        int amount = paymentInfo.getAmount();
        int vat = paymentInfo.getVat();
        paymentInfo.setAmount(amount - cancelPaymentInfo.getAmount());
        paymentInfo.setVat(vat - cancelPaymentInfo.getVat());
    }

    /**
     * 부가가치세 계산 및 검증
     * @param amountInfo 금액과 부가가치세 정보
     */
    private boolean validationVAT(AmountInfo amountInfo) {

        if(amountInfo.getVat() == null)
            amountInfo.setVat(DataConvertor.calVAT(amountInfo.getAmount()));

        if(amountInfo.getVat() > amountInfo.getAmount())
            return false;   // 부가가치세는 결제금액보다 클수 없습니다.
        return true;
    }

    /**
     * 취소 시 VAT 계산 및 검증
     * @param paymentInfo 원 결제 정보
     * @param cancelAmountInfo    취소 금액정보
     * @return
     */
    private boolean validationCancelVAT(PaymentInfo paymentInfo, AmountInfo cancelAmountInfo) {

        if(cancelAmountInfo.getVat() == null){
            if(paymentInfo.getAmount() == cancelAmountInfo.getAmount())	// 원 결제금액과 취소금액이 같다면 원 결제의 남은 부가가치세를 사용
                cancelAmountInfo.setVat(paymentInfo.getVat());
			else
                cancelAmountInfo.setVat(DataConvertor.calVAT(cancelAmountInfo.getAmount()));
        }
        if(cancelAmountInfo.getVat() > cancelAmountInfo.getAmount())
            return false;   // 부가가치세는 결제금액보다 클수 없습니다.
        return true;
    }

    /**
     * 취소 결제를 하기전 데이터 검증
     * @param paymentInfo 원 결제 정보
     * @param cancelAmountInfo 취소 결제 정보
     * @return
     */
    private boolean validationCancelData(PaymentInfo paymentInfo, AmountInfo cancelAmountInfo){

        if(paymentInfo.getAmount() < cancelAmountInfo.getAmount()) // 원거래 금액보다 취소 금액이 큰 값인지 검증
            return false;

        if(paymentInfo.getVat() < cancelAmountInfo.getVat() )   // 원거래 부가가치세보다 취소 부가가치세가 큰 값인지 검증
            return false;

        if((paymentInfo.getAmount() == cancelAmountInfo.getAmount())    // 원거래 금액이 0원이 될 때 부가가치세는 차감된 후는 0이어야 함.
            && (paymentInfo.getVat() != cancelAmountInfo.getVat()))
                return false;

        return true;
    }

    /**
     * 결제 string 데이터 명세 생성
     * @param requestDto 결제 string 데이터 명세를 생성하기 위한 필요 데이터
     * @param secretKey 카드정보를 암호화하기 위한 secret key
     * @return
     */
    public String generatePayStatement(RequestDto requestDto, String secretKey) throws Exception{

        String concatCardInfo = DataConvertor.concatCardInfo(requestDto.getCardInfo());   // 카드정보 문자열 합치기
        String encData = AES256Cipher.encryptCardInfo(concatCardInfo, secretKey);       // 카드정보 암호화

        // 공통헤더부문
        String payType = DataConvertor.formatPayStr(requestDto.getPayType(), Constants.PayStatementSize.PAY_TYPE, Constants.DATA_TYPE_STRING);
        String manageId = DataConvertor.formatPayStr(requestDto.getManageId(), Constants.PayStatementSize.MANAGE_ID, Constants.DATA_TYPE_STRING);
        String cardNum = DataConvertor.formatPayStr(requestDto.getCardInfo().getCardNumber(), Constants.PayStatementSize.CARD_NUMBER, Constants.DATA_TYPE_NUM_LEFT);
        
        // 데이터부문
        String installment = DataConvertor.formatPayStr(String.valueOf(requestDto.getAmountInfo().getInstallment()), Constants.PayStatementSize.INSTALLMENT, Constants.DATA_TYPE_NUM_ZERO);
        String expDate = DataConvertor.formatPayStr(requestDto.getCardInfo().getExpirationDate(), Constants.PayStatementSize.EXP_DATE, Constants.DATA_TYPE_NUM_LEFT);
        String cvc = DataConvertor.formatPayStr(requestDto.getCardInfo().getCvc(), Constants.PayStatementSize.CVC, Constants.DATA_TYPE_NUM_LEFT);
        String amount = DataConvertor.formatPayStr(String.valueOf(requestDto.getAmountInfo().getAmount()), Constants.PayStatementSize.AMOUNT, Constants.DATA_TYPE_NUM);
        String vat = DataConvertor.formatPayStr(String.valueOf(requestDto.getAmountInfo().getVat().intValue()), Constants.PayStatementSize.VAT, Constants.DATA_TYPE_NUM_ZERO);
        String originManageId = DataConvertor.formatPayStr("", Constants.PayStatementSize.ORIGIN_MNG_ID, Constants.DATA_TYPE_STRING);
        String encCardInfo = DataConvertor.formatPayStr(encData, Constants.PayStatementSize.ENC_CARD_INFO, Constants.DATA_TYPE_STRING);
        String reserved = DataConvertor.formatPayStr("", Constants.PayStatementSize.RESERVED, Constants.DATA_TYPE_STRING);

        // 데이터 명세 생성
        StringBuilder sb = new StringBuilder();
        sb.append(payType).append(manageId).append(cardNum).append(installment).append(expDate).append(cvc)
                .append(amount).append(vat).append(originManageId).append(encCardInfo).append(reserved);
        String concatStr = sb.toString();
        return DataConvertor.formatPayStr(String.valueOf(concatStr.length()), Constants.PayStatementSize.DATA_SIZE, Constants.DATA_TYPE_NUM) + concatStr;
    }

    /**
     * string 명세에서 CardInfo 객체로 추출
     * @param payStatement string 명세
     * @return CardInfo 객체
     */
    private CardInfo extractCardInfo(String payStatement) throws Exception{

        int encOffset = Constants.PayStatementSize.TOT_SIZE - Constants.PayStatementSize.ENC_CARD_INFO - Constants.PayStatementSize.RESERVED;   // 암호화된 카드정보 위치
        String encCardInfo = payStatement.substring(encOffset, Constants.PayStatementSize.ENC_CARD_INFO);   // 암호화된 정보 추출
        int indexBlank = encCardInfo.indexOf(' ');
        encCardInfo = encCardInfo.substring(0, indexBlank); // 암호화된 카드 string 값만 추출
        log.debug("encCardInfo = " + encCardInfo);

        String decData = AES256Cipher.decryptCardInfo(encCardInfo, secretKey);  // 복호화
        log.debug("decData = " + decData);

        return DataConvertor.objectCardInfo(decData);   // 카드정보 객체 변환
    }

    /**
     * 카드번호 마스킹
     * @param cardInfo
     */
    private void maskingCardInfo(CardInfo cardInfo) {
        String cardNumber = cardInfo.getCardNumber();
        StringBuffer sb = new StringBuffer(cardNumber.substring(0, 6));

        int midSize = cardNumber.length() - 9;
        while(midSize > 0) {
            sb.append('*');
            midSize--;
        }
        sb.append(cardNumber.substring(cardNumber.length() - 3, cardNumber.length()));
        cardInfo.setCardNumber(sb.toString());
    }
}
