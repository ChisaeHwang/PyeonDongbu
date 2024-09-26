package com.pyeondongbu.editorrecruitment.domain.recruitment.dto;

import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.Payment;
import com.pyeondongbu.editorrecruitment.domain.recruitment.domain.type.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    @NotNull(message = "급료 지금 방식은 선택되지 않을 수 없습니다.")
    private PaymentType type;

    @NotBlank(message = "금액은 공백이 될 수 없습니다.")
    @Size(max = 8, message = "금액은 최대 8자리의 숫자여야 합니다.")
    private String amount;


    public Payment toEntity() {
        return new Payment(type, amount);
    }

    public static PaymentDTO from (
            final Payment payment
    ) {
        return new PaymentDTO(payment.getType(), payment.getAmount());
    }
}
