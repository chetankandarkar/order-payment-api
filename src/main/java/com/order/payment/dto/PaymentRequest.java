package com.order.payment.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PaymentRequest {

    private String paymentReferenceId;
    private BigDecimal amount;

}
