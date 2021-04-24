package com.megait.soir.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Cody {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    private Member member;

    private long outerId;

    private long topId;

    private long bottomId;

    private long accId;

    private long shoesId;

    private String backgroundId;

    private long outerSize;

    private long topSize;

    private long bottomSize;

    private long shoesSize;

    private long accSize;

}
