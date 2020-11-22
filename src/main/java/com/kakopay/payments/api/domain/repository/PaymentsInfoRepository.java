package com.kakopay.payments.api.domain.repository;

import com.kakopay.payments.api.domain.entity.PaymentsInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentsInfoRepository extends JpaRepository<PaymentsInfo, String> {
}
