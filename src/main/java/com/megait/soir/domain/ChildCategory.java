package com.megait.soir.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder @AllArgsConstructor
@NoArgsConstructor
public class ChildCategory {

    @Id @GeneratedValue
    private long id;

    private String name;

    /**
     * 수정
     */
    // Item
    @OneToMany(mappedBy = "childCategory", cascade={CascadeType.ALL})
    private List<Item> items = new ArrayList<>();

    // ParentCategory
    @ManyToOne(cascade = CascadeType.ALL)
    private ParentCategory parentCategory;
}
















