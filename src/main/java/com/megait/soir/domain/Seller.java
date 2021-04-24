package com.megait.soir.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter @Setter
public class Seller {
    @Id @GeneratedValue
    private Long id;

    private String companyName;
    private String companyAddress;
    private String companyNumber;

    // many to one Member
    @ManyToOne
    private Member member;
}
