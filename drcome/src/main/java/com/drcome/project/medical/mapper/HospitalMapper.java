package com.drcome.project.medical.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.drcome.project.medical.service.DoctorVO;
import com.drcome.project.medical.service.NoticeVO;

public interface HospitalMapper {
	
	/* 대시보드 */
	//진료및예약리스트
	public List<Map<String, Object>> selectTodayReserve(String hospitalId);
	
	//QnA답변O
	public List<Map<String, Object>> selectQnAO(String hospitalId);
	
	//QnA답변X
	public List<Map<String, Object>> selectQnAX(String hospitalId);
	
	/* 예약내역 - clinic */
	//Main
	public List<Map<String, Object>> selectReserveMain(String hospitalId, String date, String reserveKindstatus);
	
	//Dr
	public List<Map<String, Object>> selectReserveDr(String hospitalId, Integer doctorNo);
	
	/* QnA */
	//QnA 전체
	public List<Map<String, Object>> selectQnaList(String hospitalId);
	
	//QnA 단건상세
	public List<Map<String, Object>> selectQnaInfo(String hospitalId, Integer qnaNo);
	
	/* 공지사항 */
	//공지사항 전체
	public List<Map<String, Object>> selectNoticeList(@Param("page") int page, @Param("hospitalId") String hospitalId);
	
	//공지사항 단건상세
	public List<NoticeVO> selectNoList(String hospitalId, Integer noticeNo);
	
	//공지사항 등록 + 첨부파일 등록
	public int insertNotice(NoticeVO vo);
	public int insertAttach(NoticeVO vo);
	
	/* 병원프로필 */
	//병원-의사 조회
	public List<DoctorVO> selectDrList(String hospitalId);
	
	/* 환자리스트 */
	//환자 조회
	public List<Map<String, Object>> selectPatientList(String hospitalId);
	
	//환자 진료 상세 조회
	public List<Map<String, Object>> selectPatientDetailList(String hospitalId, Integer patientNo);
	
	/* ------- Total Count ------- */
	/* 공지사항 리스트 페이징 */
	public int noticeCount(@Param("hospitalId") String hospitalId);
}
