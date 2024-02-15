package com.drcome.project.main.service;

import lombok.Data;

@Data
public class PaymentVO {
	
	private Integer reserveNo;
	private Integer paymentNo;
	private String orderNum;
	private Integer amount;
	private String userId;
	private String paymentMethod;
	
}
