package com.kakopay.payments.api.domain.repository;

import com.kakopay.payments.api.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
