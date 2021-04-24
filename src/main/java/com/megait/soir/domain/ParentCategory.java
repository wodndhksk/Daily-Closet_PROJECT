package com.megait.soir.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParentCategory {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    /**
     *  수정
     */

    // Item
    @OneToMany(mappedBy = "parentCategory", cascade={CascadeType.ALL})
    private List <Item> items = new ArrayList<>();

    // ChildCategory
    @OneToMany(mappedBy = "parentCategory", cascade={CascadeType.ALL})
    private List <ChildCategory> childCategories = new ArrayList<>();
}


