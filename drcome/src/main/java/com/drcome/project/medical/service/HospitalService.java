package com.drcome.project.medical.service;

import java.util.List;

import com.drcome.project.admin.domain.Hospital;

public interface HospitalService {
	
	//병원 단건조회(id로)
	public Hospital findByhospitalId(String hospitalId);
	
	//병원-의사 조회
	public List<DoctorVO> getDoctorAll(String hospitalId);
}