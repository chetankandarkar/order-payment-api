package com.order.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.payment.dto.PaymentRequest;
import com.order.payment.dto.PaymentResponse;
import com.order.payment.service.PaymentService;
import com.order.payment.util.LoggerUtil;

@RestController
@RequestMapping("/api/orders")
public class PaymentController {

	@Autowired
	LoggerUtil log;

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping("/{orderId}/payments")
	public ResponseEntity<PaymentResponse> pay(@PathVariable Long orderId, @RequestBody PaymentRequest request) {

		log.doLog(1, "Received payment request | orderId={} | referenceId={}", orderId,
				request.getPaymentReferenceId());

		PaymentResponse response = paymentService.processPayment(orderId, request);

		log.doLog(1, "Payment response sent | orderId={} | referenceId={}", orderId, request.getPaymentReferenceId());

		return ResponseEntity.ok(response);
	}
}
