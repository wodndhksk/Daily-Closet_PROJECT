package com.megait.soir.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItem {
    // N:M 관계를 해소하기 위한 중간 mapping table

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Orders order;

    // @OneToOne이면 한 Item은 한 OrderItem밖에 되지 못한다.
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    private int orderPrice;

    private int count;
}
