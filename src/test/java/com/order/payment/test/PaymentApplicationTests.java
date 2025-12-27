package com.order.payment.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.order.payment.constant.OrderStatus;
import com.order.payment.dto.PaymentRequest;
import com.order.payment.dto.PaymentResponse;
import com.order.payment.exception.BadRequestException;
import com.order.payment.model.Order;
import com.order.payment.model.Payment;
import com.order.payment.repository.OrderRepository;
import com.order.payment.repository.PaymentRepository;
import com.order.payment.service.PaymentServiceImpl;
import com.order.payment.util.LoggerUtil;

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceTest {

	@Autowired
	private PaymentServiceImpl paymentService;

	@MockBean
	private OrderRepository orderRepository;

	@MockBean
	private PaymentRepository paymentRepository;

	@MockBean
	private LoggerUtil log;

	private Order order;

	@BeforeEach
	void setup() {
		order = new Order();
		order.setId(1L);
		order.setStatus(OrderStatus.CREATED);
		order.setTotalAmount(BigDecimal.valueOf(100));
	}

	@Test
	void shouldProcessPaymentSuccessfully() {
		when(paymentRepository.findByPaymentReferenceId("REF123")).thenReturn(Optional.empty());
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		PaymentRequest request = new PaymentRequest();
		request.setPaymentReferenceId("REF123");
		request.setAmount(BigDecimal.valueOf(100));

		PaymentResponse response = paymentService.processPayment(1L, request);

		assertEquals("SUCCESS", response.getStatus());
		assertEquals("REF123", response.getPaymentReferenceId());
	}

	@Test
	void shouldReturnPreviousResponseForDuplicatePayment() {
		Payment payment = new Payment();
		payment.setPaymentReferenceId("REF124");
		payment.setAmount(BigDecimal.valueOf(100));
		payment.setOrder(order);

		when(paymentRepository.findByPaymentReferenceId("REF124")).thenReturn(Optional.of(payment));

		PaymentRequest request = new PaymentRequest();
		request.setPaymentReferenceId("REF124");
		request.setAmount(BigDecimal.valueOf(100));

		PaymentResponse response = paymentService.processPayment(1L, request);

		assertEquals("SUCCESS", response.getStatus());
		assertEquals("REF124", response.getPaymentReferenceId());
	}

	@Test
	void shouldThrowExceptionForAmountMismatch() {
		when(paymentRepository.findByPaymentReferenceId("REF125")).thenReturn(Optional.empty());
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		PaymentRequest request = new PaymentRequest();
		request.setPaymentReferenceId("REF125");
		request.setAmount(BigDecimal.valueOf(50));

		assertThrows(BadRequestException.class, () -> paymentService.processPayment(1L, request));
	}
}