package com.megait.soir.domain;

import com.megait.soir.converter.ItemImageListConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Item {

    @Id
    private Long id;

    private String brand;

    private String name;

    private long price;

    private String code;

    private String img_name;

    @Convert(converter = ItemImageListConverter.class)
    @Column(length = 2048)
    private List<String> urls = new ArrayList<>();

    @Convert(converter = ItemImageListConverter.class)
    @Column(length = 2048)
    private List<String> sizes = new ArrayList<>();

    private int liked;

    /**
     * 수정
     */
    @ManyToOne(cascade = CascadeType.ALL)
    private ParentCategory parentCategory;

    @ManyToOne(cascade = CascadeType.ALL)
    private ChildCategory childCategory;

    @OneToMany(mappedBy = "item")
    private List<Review> review  = new ArrayList<>();;

    @Transient
    private String mainUrl;



}
