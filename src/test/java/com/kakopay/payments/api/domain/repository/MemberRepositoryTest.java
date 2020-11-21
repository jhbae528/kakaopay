package com.kakopay.payments.api.domain.repository;

import com.kakopay.payments.api.domain.entity.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    public void cleanup(){
        System.out.println("cleanup");
        memberRepository.deleteAll();
    }

    @Test
    void insertData() {
        memberRepository.save(Member.builder()
                .name("배재호")
                .email("jhbae5280@gmail.com")
                .build());

        List<Member> memberList = memberRepository.findAll();
        Member member = memberList.get(0);
        assertEquals(member.getId(), 1);
        assertEquals(member.getName(), "배재호");
        assertEquals(member.getEmail(), "jhbae5280@gmail.com");
    }
}