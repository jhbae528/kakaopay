package com.kakaopay.payments.api.domain.repository;

import com.kakaopay.payments.api.domain.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, String> {
    List<PaymentInfo> findByOriginManageId(String name);
}
