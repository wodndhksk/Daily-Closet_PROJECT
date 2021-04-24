package com.megait.soir.domain;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInfo {

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
