package com.megait.soir.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Category {

    // fields
    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable( // mapping table을 만들어주는 annotation
            name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items = new ArrayList<>();
    // 즉, items는 DB에서 category id와 item id를 합친 table이 된다?


    @ManyToOne(fetch = FetchType.LAZY) // Lazy Loading 사용
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children;



    // methods
    public Category addChildCategory(Category child){
        if(child == null) {
            return this;
        }

        if(children == null){
            this.children = new ArrayList<>();
        }

        children.add(child);
        child.parent = this;
        return this;
    }

    public Category setParent(Category parent){
        if(parent == null){
            return this;
        }

        this.parent = parent;
        parent.addChildCategory(this);
        return this;
    }
}
