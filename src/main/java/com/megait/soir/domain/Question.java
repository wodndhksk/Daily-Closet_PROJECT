package com.megait.soir.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Question {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionTitle;
    private String questionContent;
    private LocalDateTime questionDate;

    // many to one Member
    @ManyToOne
    private Member member;
    // one to one Answer
    @OneToOne
    private Answer answer;
}
