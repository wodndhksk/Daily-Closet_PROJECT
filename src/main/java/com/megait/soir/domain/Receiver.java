package com.megait.soir.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Receiver {
    private String name;
    private String phone;
    private String email;
}
