package com.order.payment.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(name = "payments", uniqueConstraints = @UniqueConstraint(columnNames = "paymentReferenceId"))
@Data
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String paymentReferenceId;

	private BigDecimal amount;

	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;

}
