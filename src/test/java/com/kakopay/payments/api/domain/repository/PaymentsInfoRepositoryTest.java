package com.kakopay.payments.api.domain.repository;

import com.kakopay.payments.api.domain.entity.CancelInfo;
import com.kakopay.payments.api.domain.entity.PaymentsInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PaymentsInfoRepositoryTest {

    @Autowired
    PaymentsInfoRepository paymentsInfoRepository;

    @Test
    void insertData() {
        PaymentsInfo newPaymentsInfo = PaymentsInfo.builder()
                .installment(0)
                .payType("PAYMENT")
                .payStatement("abcde")
                .amount(1000)
                .vat(100)
                .build();

        PaymentsInfo savedPayment = saveDB(newPaymentsInfo);
        PaymentsInfo selectedInfo = (getPaymentsInfo(savedPayment.getManageId()));

        CancelInfo newCancelInfo = CancelInfo.builder()
                .cancelType("PART")
                .cancelAmount(200)
                .cancelVat(20)
                .build();

        PaymentsInfo updatedPaymentsInfo = updateCancelInfo(selectedInfo.getManageId(), newCancelInfo);

        CancelInfo newCancelInfo2 = CancelInfo.builder()
                .cancelType("PART")
                .cancelAmount(300)
                .cancelVat(30)
                .build();

        PaymentsInfo updated2PaymentsInfo = updateCancelInfo(updatedPaymentsInfo.getManageId(), newCancelInfo2);

        PaymentsInfo selectedInfo2 = (getPaymentsInfo(updated2PaymentsInfo.getManageId()));

        assertEquals("P0000000000000000001", selectedInfo2.getManageId());
    }


    @Transactional
    private PaymentsInfo saveDB(PaymentsInfo paymentsInfo){
        PaymentsInfo savedInfo = paymentsInfoRepository.save(paymentsInfo);
        return savedInfo;
    }

    @Transactional
    private PaymentsInfo getPaymentsInfo(String id){
        Optional<PaymentsInfo> optionalInfo = paymentsInfoRepository.findById(id);
        if(optionalInfo.isPresent()){
            PaymentsInfo info = optionalInfo.get();
            return info;
        }
        return null;
    }

    @Transactional
    private PaymentsInfo updateCancelInfo(String id, CancelInfo cncelInfo){
        Optional<PaymentsInfo> optionalInfo = paymentsInfoRepository.findById(id);

        if(optionalInfo.isPresent()){
            PaymentsInfo info = optionalInfo.get();
            info.addCancelInfo(cncelInfo);
            PaymentsInfo updatedInfo = paymentsInfoRepository.save(info);
            return updatedInfo;
        }
        return null;
    }
}