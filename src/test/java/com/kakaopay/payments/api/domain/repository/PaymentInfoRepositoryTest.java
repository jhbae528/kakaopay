package com.kakaopay.payments.api.domain.repository;

import com.kakaopay.payments.api.domain.entity.PaymentInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;

@SpringBootTest
class PaymentInfoRepositoryTest {

    @Autowired
    PaymentInfoRepository paymentInfoRepository;

    @Test
    void insertData() {
        PaymentInfo newPaymentsInfo = new PaymentInfo();
        newPaymentsInfo.setInstallment(0);
        newPaymentsInfo.setPayType("PAYMENT");
        newPaymentsInfo.setPayStatement("ABCDEFG");
        newPaymentsInfo.setAmount(1000);
        newPaymentsInfo.setVat(100);

        PaymentInfo savedPayment = saveDB(newPaymentsInfo);
        PaymentInfo selectedInfo = (getPaymentsInfo(savedPayment.getManageId()));
    }


    @Transactional
    private PaymentInfo saveDB(PaymentInfo paymentsInfo){
        PaymentInfo savedInfo = paymentInfoRepository.save(paymentsInfo);
        return savedInfo;
    }

    @Transactional
    private PaymentInfo getPaymentsInfo(String id){
        Optional<PaymentInfo> optionalInfo = paymentInfoRepository.findById(id);
        if(optionalInfo.isPresent()){
            PaymentInfo info = optionalInfo.get();
            return info;
        }
        return null;
    }

    @Transactional
    private PaymentInfo updateCancelInfo(String id){
        Optional<PaymentInfo> optionalInfo = paymentInfoRepository.findById(id);

        if(optionalInfo.isPresent()){
            PaymentInfo info = optionalInfo.get();
            //info.addCancelInfo(cncelInfo);
            PaymentInfo updatedInfo = paymentInfoRepository.save(info);
            return updatedInfo;
        }
        return null;
    }
}