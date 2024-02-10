package com.drcome.project.main.service;

import java.util.Date;

import lombok.Data;

@Data
public class ReservationVO {
	private Integer reserveNo;
	private String reserveYear;
	private String reserveTime;
	private Date bookingDate;
	private String doctorNo;
	private String reserveMonth;
	private String reserveDay;
	private String symptom;
	private String reserveKind;
	private String reserveStatus;
	private String userId;
	private String hospitalId;
}