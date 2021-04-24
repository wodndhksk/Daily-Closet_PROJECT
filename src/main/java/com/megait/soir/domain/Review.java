package com.megait.soir.domain;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String img;

    private long parentId;

    @ManyToOne
    private Item item;

    @ManyToOne
    private Member member;

    // many to one Orders
    @ManyToOne
    private Orders order;

    private String createDate;

    private String updateDate;

}
