package com.drcome.project.medical.service;

import java.util.List;
import java.util.Map;

import com.drcome.project.admin.domain.Hospital;

public interface HospitalService {
	
	/* 대시보드 */
	//상단Card
	//예약현황 카운트
	public int selectReserveCnt(String hospitalId);
	public int selectQnaCnt(String hospitalId);
	public int selectPayMonth(String hospitalId);
	public int selectC2Rate(String hospitalId);
	
	//오늘의 진료현황 리스트
	public List<Map<String, Object>> getTodayReserve(String hospitalId);
	
	//QnA답변O
	public List<Map<String, Object>> getQnAO(String hospitalId);
	
	//QnA답변X
	public List<Map<String, Object>> getQnAX(String hospitalId);
	
	/* 환자리스트 */
	//환자 조회
	public List<Map<String, Object>> getPaientList(Map<String, Object> map);
	public int patientCount(Map<String, Object> map);
	
	//환자 상세 진료내역 조회
	public List<Map<String, Object>> getPaientDetailList(Map<String, Object> map);
	public int patientInfoCount(Map<String, Object> map);

	//환자 진료내역 단건 조회
	public Map<String, Object> getPaientClinicInfo(String hospitalId, Integer patientNo, Integer clinicNo);
	
	//환자 진료내역 단건 처방전 조회
	public List<Map<String, Object>> getpaientPillInfo(Integer clinicNo);
	
	/* 예약내역 - clinic */
	//Main
	public List<Map<String, Object>> getRerveList(String hospitalId, String date, String reserveKindstatus);
	
	//Dr
	public List<Map<String, Object>> getReserveDrList(String hospitalId, Integer doctorNo, String date, String reserveKindstatus);
	
	//Dr리스트
	public List<Map<String, Object>> getDrAllList(String hospitalId);
	
	//약국 리스트 받아오기
	public List<Map<String, Object>> selectPharList(Map<String, Object> map);
	
	//약국 전송 Y 업데이트
	public int updateSendPersStatus(List<Map<String, Long>> map);
	
	//약국에 전송 후 상태값 변경
	public void updateReservationStatus(Map<String, Object> parameter);
	
	/* QnA */
	//QnA 전체
	public List<Map<String, Object>> getQnaList(Map<String, Object> map);
	public int qnaCount(Map<String, Object> map);
	
	//QnA 단건상세
	public QnaVO getQnaInfo(QnaVO qnaVO);
	
	//Answer 단건상세
	public QnaVO getAnsInfo(QnaVO qnaVO);
	
	//QnA 파일 가져오기
	public List<NoticeAttachVO> selectQnaAtt(NoticeAttachVO attVO);
	
	//QnA 답변 인서트 + 첨부파일 인서트 + 기존 QnA 상태 업데이트
	public int insertQnaAns(QnaVO qnaVO);
	public int updateQnaStatus(QnaVO qnaVO);
	public int insertAttachQnaAns(QnaVO qnaVO);
	
	//QnA User 질문 인서트 + 첨부파일 인서트
	public int insertQnaMem(QnaVO qnaVO);
	
	/* 공지사항 */
	//공지사항 전체	
	public List<Map<String, Object>> getNoticeList(int page, int type, String keyword, String hospitalId);
	public int noticeCount(int type, String keyword, String hospitalId);
	
	//공지사항 단건상세
	public NoticeVO getNoticeDetail(NoticeVO noticeVO);
	
	//공지사항 등록
	public int insertNoticeInfo(NoticeVO vo);
	public int insertAttach(NoticeVO vo);
	
	//공지사항 수정
	public int updateNotice(NoticeVO vo);
	public int deleteAttachment(int noticeNo);
	
	//공지사항 삭제
	public void deleteNotice(NoticeVO vo);
	
	/* 병원프로필 */
	//병원 단건조회(id로)
	public Hospital findByhospitalId(String hospitalId);
	
	//병원-의사 조회
	public List<DoctorVO> getDoctorAll(String hospitalId);
	
	// 의사 번호 조회
	public int getCurrentDoctorNo();
	
	// 의사 정보 조회
	public DoctorVO selectDoctor(int doctorNo);
	
	// 의사 등록
	public int insertDoctor(DoctorVO vo);
	
	// 의사 수정
	public int updateDoctor(DoctorVO vo);

}
