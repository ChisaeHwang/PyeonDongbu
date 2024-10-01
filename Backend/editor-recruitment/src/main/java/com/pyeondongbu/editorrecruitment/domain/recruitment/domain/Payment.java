package com.pyeondongbu.editorrecruitment.domain.recruitment.domain;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.type.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class Payment {

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type")
    private PaymentType type;

    @Column(name = "payment_amount", nullable = false)
    private String amount;


}

