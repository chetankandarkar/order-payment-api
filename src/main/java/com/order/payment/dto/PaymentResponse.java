package com.order.payment.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    private Long orderId;
    private String paymentReferenceId;
    private String status;
    private BigDecimal amount;

}
