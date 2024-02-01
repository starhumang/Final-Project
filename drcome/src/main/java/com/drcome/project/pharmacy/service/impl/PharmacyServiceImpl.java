package com.drcome.project.pharmacy.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drcome.project.pharmacy.PharmacyVO;
import com.drcome.project.pharmacy.mapper.PharmacyMapper;
import com.drcome.project.pharmacy.service.PharmacyService;

@Service
public class PharmacyServiceImpl implements PharmacyService{

	@Autowired
	PharmacyMapper mapper;
	
	@Override
	public PharmacyVO selectPharmacyInfo(PharmacyVO pharmacyVO) {
		return mapper.selectPharmacyInfo(pharmacyVO);
	}

	@Override
	public List<Map<String, Object>> selectPrescriptionList() {
		List<Map<String, Object>> listmap = mapper.selectPrescriptionList();
		return listmap;
	}

}