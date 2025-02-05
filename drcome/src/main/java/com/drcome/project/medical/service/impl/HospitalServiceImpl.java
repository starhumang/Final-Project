package com.drcome.project.medical.service.impl;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drcome.project.admin.domain.Hospital;
import com.drcome.project.medical.mapper.HospitalMapper;
import com.drcome.project.medical.repository.HospitalRepository;
import com.drcome.project.medical.service.DoctorTimeVO;
import com.drcome.project.medical.service.DoctorVO;
import com.drcome.project.medical.service.HospitalService;
import com.drcome.project.medical.service.NoticeAttachVO;
import com.drcome.project.medical.service.NoticeVO;
import com.drcome.project.medical.service.QnaVO;

@Service
public class HospitalServiceImpl implements HospitalService {

	@Autowired
	HospitalRepository hrepo;
	@Autowired
	HospitalMapper hospitalMapper;

	/* 대시보드 */
	//상단Card
	//예약현황 카운트
	@Override
	public int selectReserveCnt(String hospitalId) {
		return hospitalMapper.selectReserveCnt(hospitalId);
	}
	@Override
	public int selectQnaCnt(String hospitalId) {
		return hospitalMapper.selectQnaCnt(hospitalId);
	}
	
	@Override
	public int selectPayMonth(String hospitalId) {
		return hospitalMapper.selectPayMonth(hospitalId);
	}
	
	@Override
	public int selectC2Rate(String hospitalId) {
		return hospitalMapper.selectC2Rate(hospitalId);
	}
	
	// 오늘의 진료 내역 리스트
	@Override
	public List<Map<String, Object>> getTodayReserve(String hospitalId) {
		List<Map<String, Object>> listToday = hospitalMapper.selectTodayReserve(hospitalId);
		return listToday;
	}

	// QnA답변O
	@Override
	public List<Map<String, Object>> getQnAO(String hospitalId) {
		List<Map<String, Object>> listQnAO = hospitalMapper.selectQnAO(hospitalId);
		return listQnAO;
	}

	// QnA답변X
	@Override
	public List<Map<String, Object>> getQnAX(String hospitalId) {
		List<Map<String, Object>> listQnAX = hospitalMapper.selectQnAX(hospitalId);
		return listQnAX;
	}

	/* 환자리스트 */
	// 환자 조회
	@Override
	public List<Map<String, Object>> getPaientList(Map<String, Object> map) {
		List<Map<String, Object>> listPa = hospitalMapper.selectPatientList(map);
		return listPa;
	}
	
	// 환자 페이징
	@Override
	public int patientCount(Map<String, Object> map) {
		return hospitalMapper.patientCount(map);
	}

	// 환자 상세 조회
	@Override
	public List<Map<String, Object>> getPaientDetailList(Map<String, Object> map) {
		List<Map<String, Object>> listPaDe = hospitalMapper.selectPatientDetailList(map);
		return listPaDe;
	}
	
	// 환자 상세 페이징
	@Override
	public int patientInfoCount(Map<String, Object> map) {
		return hospitalMapper.patientInfoCount(map);
	}

	// 환자 진료내역 단건 조회
	@Override
	public Map<String, Object> getPaientClinicInfo(String hospitalId, Integer patientNo, Integer clinicNo) {
		Map<String, Object> listClinicInfo = hospitalMapper.selectPatientInfo(hospitalId, patientNo, clinicNo);
		return listClinicInfo;
	}

	// 환자 진료내역 단건 처방전 조회
	@Override
	public List<Map<String, Object>> getpaientPillInfo(Integer clinicNo) {
		List<Map<String, Object>> listClinicPill = hospitalMapper.selectPillList(clinicNo);
		return listClinicPill;
	}

	/* 예약내역 - clinic */
	//Main
	@Override
	public List<Map<String, Object>> getRerveList(String hospitalId, String date, String reserveKindstatus) {
		List<Map<String, Object>> listReserveAll = hospitalMapper.selectReserveMain(hospitalId, date, reserveKindstatus);
		return listReserveAll;
	}

	//Dr
	@Override
	public List<Map<String, Object>> getReserveDrList(String hospitalId, Integer doctorNo, String date, String reserveKindstatus) {
		List<Map<String, Object>> listReserveDr = hospitalMapper.selectReserveDr(hospitalId, doctorNo, date, reserveKindstatus);
		return listReserveDr;
	}
	//Dr리스트
	@Override
	public List<Map<String, Object>> getDrAllList(String hospitalId) {
		List<Map<String, Object>> listDrAll = hospitalMapper.allDrList(hospitalId);
		return listDrAll;
	}
	
	//선택 약국 받아오기
	@Override
	public List<Map<String, Object>> selectPharList(Map<String, Object> map) {
		List<Map<String, Object>> listPhar = hospitalMapper.selectPharList(map);
		return listPhar;
	}
	
	//업데이드 전송값
	@Transactional
	@Override
	public int updateSendPersStatus(List<Map<String, Long>> list) {
		int result = 0;
		for(Map<String, Long> map :list )
			result += hospitalMapper.updateSendPersStatus(map);
		//상태변경하기
		
		return result;
	}
	
	//약국 전송 후 상태 업데이트
	@Override
	public void updateReservationStatus(Map<String, Object> parameter) {
		hospitalMapper.updateReservationStatus(parameter);
	}
	
	/* QnA */
	// QnA 전체
	@Override
	public List<Map<String, Object>> getQnaList(Map<String, Object> map) {
		List<Map<String, Object>> listQnaAll = hospitalMapper.selectQnaList(map);
		return listQnaAll;
	}
	
	// QnA 페이징
	@Override
	public int qnaCount(Map<String, Object> map) {
		return hospitalMapper.qnaCount(map);
	}

	// QnA 단건상세
	@Override
	public QnaVO getQnaInfo(QnaVO qnaVO) {
		QnaVO qnaInfo = hospitalMapper.selectQnaInfo(qnaVO);
		return qnaInfo;
	}
	// Answer 단건상세
	@Override
	public QnaVO getAnsInfo(QnaVO qnaVO) {
		QnaVO ansInfo = hospitalMapper.selectAnsInfo(qnaVO);
		return ansInfo;
	}
	
	// select 첨부파일 보기
	@Override
	public List<NoticeAttachVO> selectQnaAtt(NoticeAttachVO attVO) {
		List<NoticeAttachVO> qnaAtt = hospitalMapper.selectQnaAtt(attVO);
		return qnaAtt;
	}
	
	// Answer 등록
	@Override
	public int insertQnaAns(QnaVO qnaVO) {
		return hospitalMapper.insertQnaAns(qnaVO);
	}

	// 기존 QnA 상테 업데이트
	@Override
	public int updateQnaStatus(QnaVO qnaVO) {
		return hospitalMapper.updateQnaStatus(qnaVO);
	}

	// Answer&Question 첨부파일 등록
	@Override
	public int insertAttachQnaAns(QnaVO qnaVO) {
		return hospitalMapper.insertAttachQnaAns(qnaVO);
	}

	//QnA User 질문 인서트 + 첨부파일 인서트
	@Override
	public int insertQnaMem(QnaVO qnaVO) {
		return hospitalMapper.insertQnaMem(qnaVO);
	}
	
	/* 공지사항 */
	// 공지사항 전체
	@Override
	public List<Map<String, Object>> getNoticeList(int page, int type, String keyword, String hospitalId) {
		List<Map<String, Object>> listNoticeAll = hospitalMapper.selectNoticeList(page, type, keyword, hospitalId);
		return listNoticeAll;
	}
	
	// 공지사항 페이징
	@Override
	public int noticeCount(int type, String keyword, String hospitalId) {
		return hospitalMapper.noticeCount(type, keyword, hospitalId);
	}
	
	// 공지사항 단건상세
	@Override
	public NoticeVO getNoticeDetail(NoticeVO noticeVO) {
		NoticeVO noticeInfo = hospitalMapper.selectNoList(noticeVO);
		return noticeInfo;
	}

	// 공지사항 등록 + 첨부파일
	@Override
	public int insertNoticeInfo(NoticeVO vo) {
		return hospitalMapper.insertNotice(vo);
	}
	@Override
	public int insertAttach(NoticeVO vo) {
		return hospitalMapper.insertAttach(vo);
	}
	
	// 공지사항 수정 + 첨부파일
	@Override
	public int updateNotice(NoticeVO vo) {
//		int result = 0;
//		int noticeNo = vo.getNoticeNo();
//		result = hospitalMapper.deleteAttachment(noticeNo);
		return hospitalMapper.updateNotice(vo);
	}
	@Override
	public int deleteAttachment(int noticeNo) {
		return hospitalMapper.deleteAttachment(noticeNo);
	}
	
	// 공지사항 삭제 + 첨부파일도 같이
	@Override
	public void deleteNotice(NoticeVO vo) {
		hospitalMapper.deleteNotice(vo);		
	}
	
	/* 병원프로필 */
	// 병원 단건조회(id로)
	@Override
	public Hospital findByhospitalId(String hospitalId) {
		Hospital hosSel = hrepo.findById(hospitalId).get();
		return hosSel;
	}

	// 병원 - 의사 조회
	@Override
	public List<DoctorVO> getDoctorAll(String hospitalId) {
		return hospitalMapper.selectDrList(hospitalId);
	}

	// 의사 번호 조회
	@Override
	public int getCurrentDoctorNo() {
		return hospitalMapper.getCurrentDoctorNo();
	}
	
	// 의사 정보 조회
	@Override
	public DoctorVO selectDoctor(int doctorNo) {
		return hospitalMapper.selectDoctor(doctorNo);
	}

	// 의사 등록
	@Override
	public int insertDoctor(DoctorVO vo) {
        int count = hospitalMapper.insertDoctor(vo);
        
        int doctorNo = hospitalMapper.getCurrentDoctorNo();

        List<DoctorTimeVO> times = vo.getTimes();
        for (DoctorTimeVO time : times) {
            String day = time.getDay();
            List<String> timeArray = time.getTimeArray();
            for (String timeSlot : timeArray) {
                hospitalMapper.insertDoctorTime(doctorNo, day, timeSlot);
            }
        }
        return count;
	}
	
	// 의사 수정
	@Override
	public int updateDoctor(DoctorVO vo) {
		int count = hospitalMapper.updateDoctor(vo);
		
		int doctorNo = vo.getDoctorNo();
		
		hospitalMapper.deleteDoctorTime(doctorNo);
		
		List<DoctorTimeVO> times = vo.getTimes();
        for (DoctorTimeVO time : times) {
            String day = time.getDay();
            List<String> timeArray = time.getTimeArray();
            for (String timeSlot : timeArray) {
                hospitalMapper.insertDoctorTime(doctorNo, day, timeSlot);
            }
        }
		return count;
	}



}
