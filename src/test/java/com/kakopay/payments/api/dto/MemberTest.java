package com.kakopay.payments.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    void getName() {
        MemberDto member = MemberDto.builder().name("배재호").build();

        String name = member.getName();
        assertEquals("배재호1", name);
    }
}