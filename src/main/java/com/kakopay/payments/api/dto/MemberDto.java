package com.kakopay.payments.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {
    String name;

    @Builder
    public MemberDto(String name){
        this.name = name;
    }
}
