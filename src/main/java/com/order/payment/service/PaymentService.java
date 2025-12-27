package com.order.payment.service;

import com.order.payment.dto.PaymentRequest;
import com.order.payment.dto.PaymentResponse;

public interface PaymentService {
	
    public PaymentResponse processPayment(Long orderId, PaymentRequest request);


}
