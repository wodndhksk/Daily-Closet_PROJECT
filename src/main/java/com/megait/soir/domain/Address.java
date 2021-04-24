package com.megait.soir.domain;

import lombok.*;

import javax.persistence.Embeddable;

/*
주입되는 객체 : 독립적인 entity annotation을 붙일 수 없다.
대신 embeddable annotation attach
 */
@Embeddable
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Address {
    private String city;
    private String street;
    private String zipcode;
}