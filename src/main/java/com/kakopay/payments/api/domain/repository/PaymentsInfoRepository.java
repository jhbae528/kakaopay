package com.kakopay.payments.api.domain.repository;

import com.kakopay.payments.api.domain.entity.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsInfoRepository extends JpaRepository<PaymentInfo, String> {
}
