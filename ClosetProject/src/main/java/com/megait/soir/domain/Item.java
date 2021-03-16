package com.megait.soir.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;

    private int price;

    // 재고
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories;

    private String imageUrl;

    private int liked;
}
