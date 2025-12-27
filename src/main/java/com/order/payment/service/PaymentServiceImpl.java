package com.order.payment.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.order.payment.constant.LoggerConstants;
import com.order.payment.constant.OrderStatus;
import com.order.payment.dto.PaymentRequest;
import com.order.payment.dto.PaymentResponse;
import com.order.payment.exception.BadRequestException;
import com.order.payment.exception.ResourceNotFoundException;
import com.order.payment.model.Order;
import com.order.payment.model.Payment;
import com.order.payment.repository.OrderRepository;
import com.order.payment.repository.PaymentRepository;
import com.order.payment.util.LoggerUtil;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final LoggerUtil log;

	public PaymentServiceImpl(OrderRepository orderRepository, PaymentRepository paymentRepository,LoggerUtil log) {
		this.orderRepository = orderRepository;
		this.paymentRepository = paymentRepository;
		this.log = log;
	}

	@Override
	@Transactional
	public PaymentResponse processPayment(Long orderId, PaymentRequest request) {

		log.doLog(1, "Payment processing started | orderId={} | referenceId={}", orderId,
				request.getPaymentReferenceId());

		Optional<Payment> existingPayment = paymentRepository.findByPaymentReferenceId(request.getPaymentReferenceId());

		if (existingPayment.isPresent()) {
			log.doLog(3, "Duplicate payment request detected | referenceId={}", request.getPaymentReferenceId());
			return buildResponse(existingPayment.get());
		}

		Order order = orderRepository.findById(orderId).orElseThrow(() -> {
			log.doLog(LoggerConstants.LTE, "Order not found | orderId={}", orderId);
			return new ResourceNotFoundException("Order not found");
		});

		if (order.getStatus() != OrderStatus.CREATED) {
			log.doLog(3, "Invalid order state | orderId={} | status={}", orderId, order.getStatus());
			throw new BadRequestException("Order not in CREATED state");
		}

		if (order.getTotalAmount().compareTo(request.getAmount()) != 0) {
			log.doLog(3, "Payment amount mismatch | orderId={} | expected={} | received={}", orderId,
					order.getTotalAmount(), request.getAmount());
			throw new BadRequestException("Payment amount mismatch");
		}

		Payment payment = new Payment();
		payment.setPaymentReferenceId(request.getPaymentReferenceId());
		payment.setAmount(request.getAmount());
		payment.setOrder(order);

		paymentRepository.save(payment);

		log.doLog(1, "Payment saved successfully | referenceId={}", request.getPaymentReferenceId());
		order.setStatus(OrderStatus.PAID);
		orderRepository.save(order);

		log.doLog(1, "Order status updated to PAID | orderId={}", orderId);

		return buildResponse(payment);
	}

	private PaymentResponse buildResponse(Payment payment) {
		return new PaymentResponse(payment.getOrder().getId(), payment.getPaymentReferenceId(), "SUCCESS",
				payment.getAmount());
	}
}