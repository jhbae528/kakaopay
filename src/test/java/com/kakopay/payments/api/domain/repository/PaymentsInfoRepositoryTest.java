package com.kakopay.payments.api.domain.repository;

import com.kakopay.payments.api.domain.entity.CancelInfo;
import com.kakopay.payments.api.domain.entity.PaymentInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PaymentsInfoRepositoryTest {

    @Autowired
    PaymentsInfoRepository paymentsInfoRepository;

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

        CancelInfo newCancelInfo = new CancelInfo();
        newCancelInfo.setCancelType("PART");
        newCancelInfo.setCancelAmount(200);
        newCancelInfo.setCancelVat(20);

        PaymentInfo updatedPaymentsInfo = updateCancelInfo(selectedInfo.getManageId(), newCancelInfo);

        CancelInfo newCancelInfo2 = new CancelInfo();
        newCancelInfo2.setCancelType("PART");
        newCancelInfo2.setCancelAmount(300);
        newCancelInfo2.setCancelVat(30);

        PaymentInfo updated2PaymentsInfo = updateCancelInfo(updatedPaymentsInfo.getManageId(), newCancelInfo2);

        PaymentInfo selectedInfo2 = (getPaymentsInfo(updated2PaymentsInfo.getManageId()));

        assertEquals("P0000000000000000001", selectedInfo2.getManageId());
    }


    @Transactional
    private PaymentInfo saveDB(PaymentInfo paymentsInfo){
        PaymentInfo savedInfo = paymentsInfoRepository.save(paymentsInfo);
        return savedInfo;
    }

    @Transactional
    private PaymentInfo getPaymentsInfo(String id){
        Optional<PaymentInfo> optionalInfo = paymentsInfoRepository.findById(id);
        if(optionalInfo.isPresent()){
            PaymentInfo info = optionalInfo.get();
            return info;
        }
        return null;
    }

    @Transactional
    private PaymentInfo updateCancelInfo(String id, CancelInfo cncelInfo){
        Optional<PaymentInfo> optionalInfo = paymentsInfoRepository.findById(id);

        if(optionalInfo.isPresent()){
            PaymentInfo info = optionalInfo.get();
            info.addCancelInfo(cncelInfo);
            PaymentInfo updatedInfo = paymentsInfoRepository.save(info);
            return updatedInfo;
        }
        return null;
    }
}