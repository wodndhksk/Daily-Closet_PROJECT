package com.megait.soir.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String answerTitle;
    private String answerContent;
    private LocalDateTime answerDate;
    // one to one Question
    @OneToOne
    private Question question;

}
