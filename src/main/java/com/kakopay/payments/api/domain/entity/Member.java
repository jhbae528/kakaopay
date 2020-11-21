package com.kakopay.payments.api.domain.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Data
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(length = 1000)
    private String name;

    @Column
    private String email;

    @Builder
    Member(String name, String email){
        this.name= name;
        this.email = email;
    }
}
