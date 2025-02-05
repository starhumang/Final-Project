package com.drcome.project.main.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.drcome.project.common.service.FileUploadService;
import com.drcome.project.common.service.PageDTO2;
import com.drcome.project.main.service.MainService;
import com.drcome.project.main.service.ReservationVO;
import com.drcome.project.medical.service.DoctorVO;
import com.drcome.project.medical.service.HospitalService;
import com.drcome.project.medical.service.HospitalVO;
import com.drcome.project.medical.service.NoticeVO;
import com.drcome.project.medical.service.QnaVO;
import com.drcome.project.pharmacy.PharmacyVO;

import lombok.extern.log4j.Log4j2;

/**
 * 메인페이지, 검색기능, 약국&병원 상세페이지, 약국 선택페이지, 방문예약, 비대면예약, 실시간 비대면 상담접수처리 컨트롤러
 * @author 김은별
 * 나중에 이력적기(수정일자,수정자, 수정내용안내)
 */
@Controller
@Log4j2
public class MainController {
	
	@Autowired
	MainService mainService;
	
	@Autowired
	HospitalService hospitalService;
	
   @Autowired
   private FileUploadService fileUploadService;

	/* 병원 및 약국 목록(메인페이지)
	 * return "user/home"
	 */
	@GetMapping(value ={"/", "/home"})//주소
	public String getHosList(Model model) {
		List<HospitalVO> hosList = mainService.getHosList();
		model.addAttribute("hosList", hosList);
		List<PharmacyVO> phaList = mainService.getPhaList();
		model.addAttribute("phaList", phaList);
		return "user/home";//폴더밑에 html 이름
	}
	
	
	/* 병원 상세페이지
	 * @Param HttpServletRequest request = 세션에 담긴 유저아이디
	 * @Param String hospitalId = 병원아이디
	 * @Param String page = 페이지번호
	 * @Model model =  병원정보, 의사장보, 초진기록, 당일예약기록
	 * return "user/hosDetail"
	 */
	@GetMapping("/hospitalDetail")
	public String hosInformation(HttpServletRequest request, String hospitalId, 
								Model model, 
								@RequestParam(required = false, defaultValue = "1") String page
					            ) { //String hospitalId 이게 get으로 링크에서 받은 값
		//병원정보
		HospitalVO hosInfo = mainService.getHos(hospitalId);
		model.addAttribute("hosInfo", hosInfo);
		
		//병원의 의사정보
		List<DoctorVO> docList = hospitalService.getDoctorAll(hospitalId); 
		model.addAttribute("docList", docList);
		
		//받아온 세션값
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");
		session.removeAttribute("hosId");
		session.setAttribute("hosId", hospitalId);
		
		
		//예약전 초진기록 확인
		int clinicHistory = mainService.checkClinicHistory(userId, hospitalId);
		model.addAttribute("clinicHistory", clinicHistory);
		
		//동일병원 당일 예약(진료받기전까지/ 예약한거 진료받고나면 예약ok)중복방지
		int reservationHistory = mainService.checkReservationHistory(userId, hospitalId);
		model.addAttribute("reservationHistory", reservationHistory);
		
		return "user/hosDetail";
	}

	/*병원 공지사항 상세페이징*/
	@GetMapping("/hospitalDetailP")
	@ResponseBody
	public Map<String, Object> hosInformation(HttpServletRequest request ,@RequestParam(required = false, defaultValue = "1") String page,
								 @RequestParam(required = false, defaultValue = "0") int type) {
		//공지사항
		String keyword = "";
		Map<String, Object> notice = new HashMap<>();
		
		HttpSession session = request.getSession();
		String hosId = (String) session.getAttribute("hosId");
		
		// 리스트 전체 개수
		int total = hospitalService.noticeCount(type, keyword, hosId);
		
		// 선택 페이지 변환
		int cpage = Integer.parseInt(page);

		// 페이지네이션(currentpage, total)
		PageDTO2 dto = new PageDTO2(cpage, total);

		List<Map<String, Object>> plist = hospitalService.getNoticeList(cpage, type, keyword, hosId);

		// ajax는 return으로...

		notice.put("plist", plist);
		notice.put("pagedto", dto);
		
		return notice; 
	}
	
	
	// 병원공지사항 단건상세 유저ver
	@GetMapping("/userNoticeDetail")
	   public String noticeDetail(HttpServletRequest request , NoticeVO noticeVO, Model model) {
		
		HttpSession session = request.getSession();
		String hosId = (String) session.getAttribute("hosId");
		
        noticeVO.setHospitalId(hosId);
        int noticeNo = noticeVO.getNoticeNo();
        NoticeVO noticeList = hospitalService.getNoticeDetail(noticeVO);
        model.addAttribute("hosId", hosId);
        model.addAttribute("noticeNo", noticeNo);
        model.addAttribute("noticeList", noticeList);

		return "user/userNoticeDetail";
	}
	
	
	/* 약국 상세페이지
	 * @Param String pharmacyId = 약국아이디
	 * @Model model = 약국정보
	 * return "user/phaDetail"
	 */
	@GetMapping("/pharmacyDetail")
	public String phaInformation(String pharmacyId, Model model) {
		PharmacyVO phaInfo = mainService.getPha(pharmacyId);
		model.addAttribute("phaInfo", phaInfo);
		return "user/phaDetail";
	}
	
	
	/* 병원 및 약국검색페이지
	 * @Param String word = 검색어
	 * @Model model = 검색어에 따른 병원목록, 검색어에 따른 약국목록, 검색어
	 * return "user/search"
	 */
	@GetMapping("/search")
	public String searchList(String word, Model model) {
		List<HospitalVO> searchHosList = mainService.searchHosList(word);
		model.addAttribute("searchHos", searchHosList );
		List<PharmacyVO> searchPhaList = mainService.searchPhaList(word);
		model.addAttribute("searchPha", searchPhaList);
		model.addAttribute("word", word);
		return "user/search";
	}
	
	
	/*진료과목별 병원검색
	 * @Param String mainSubject = 진료과목
	 * @Model model = 진료과목, 과목에 따른 병원목록
	 * return "user/searchSubject"
	 */
	@GetMapping("/searchSubject")
	public String searchSubject(String mainSubject, Model model) {
		List<HospitalVO> subjectHosList = mainService.searchSubjectHos(mainSubject);
		model.addAttribute("mainSubject", mainSubject);
		model.addAttribute("subject", subjectHosList);
		return "user/searchSubject";
	}
	
	
	/* 진료 완료 후, 내 근처 추천약국 리스트
	 * @@PathVariable String clinicNo = 진료번호
	 * @Model model = 진료번호, 추천약국리스트
	 */
	@GetMapping("/recommendPharmacy/{clinicNo}")
	public String PhaList(@PathVariable("clinicNo") String clinicNo, Model model) {
		List<PharmacyVO> phaList = mainService.recommendPhaList(clinicNo,10);
		model.addAttribute("clinic",clinicNo);
		model.addAttribute("phaList", phaList);
		return "user/recommendPha";
	}
	
	
	/* 처방받을 약국 선택(insert) 중복선택가능
	 * @Param Map<String, Object> data = 약국아이디(리스트), 진료번호
	 * return boolean = 성공여부(true or false)반환
	 */
	@PostMapping("/recommendPharmacy")
	@ResponseBody
	public boolean insertPhaSelect(@RequestBody Map<String, Object> data) {
		Object pharmacyIdList = data.get("pharmacyId");
		 List<String> pharmacyIdListAsList = (List<String>) pharmacyIdList;
		    
		// List를 String 배열로 변환
		 int clinicNo = Integer.parseInt((String) data.get("clinicNo"));
		 int result = 0;
		//String[] pharmacyIdArray = pharmacyIdListAsList.toArray(new String[0]);
		for(int i=0; i < pharmacyIdListAsList.size(); i++) {
			result += mainService.insertPhaSelect(pharmacyIdListAsList.get(i), clinicNo);
		}
		if(result == 0) { //insert 안되면 false
			return false;
		}else { //insert되면 true
			return true;
		}
	}
	
	
	/*방문예약페이지(select)
	 * @Param HttpServletRequest request = 세션에 담긴 유저아이디
	 * @Param String hospitalId = 병원아이디
	 * @Model model = 병원이름, 병원아이디, 의사목록, 병원휴무일
	 * return "user/contactReserve"
	 */
	@GetMapping("/contactReserve")
	public String contactReservationPage(HttpServletRequest request, String hospitalId, Model model) {
		//병원정보
		HospitalVO hosInfo = mainService.getHos(hospitalId);
		//병원이름&아이디
		model.addAttribute("hosName", hosInfo.getHospitalName());
		model.addAttribute("hospitalId", hospitalId);
		//병원의 의사정보
		List<DoctorVO> docList = hospitalService.getDoctorAll(hospitalId); 
		model.addAttribute("docList", docList);
		
		//세션으로 유저아이디 가져옴
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");
		model.addAttribute("userId", userId);
		
		String date = hosInfo.getHoliday();
		
		model.addAttribute("newDate", day(date));
		
		return "user/contactReserve";
	}
	
	
	/* 방문예약페이지(insert)
	 * @Param ReservationVO reservationVo = 예약정보VO
	 * return boolean = 성공여부(true or false)반환
	 */
	@PostMapping("/contactReserve")
	@ResponseBody
	public boolean insertReservation(@RequestBody ReservationVO reservationVo) {
		int result = mainService.insertContactReservation(reservationVo);
		if(result == 0) { //insert 안되면 false
			return false;
		}else { //insert되면 true
			return true;
		}
	}
	
	
	private List<Integer> day(String date){
		//병원 휴무일 보내기(숫자형태로 전환해서 보내는 중)
				Map<String, Integer> dayList = new HashMap<>();
				dayList.put("i1", 1);//월
				dayList.put("i2", 2);//화
				dayList.put("i3", 3);//수
				dayList.put("i4", 4);//목
				dayList.put("i5", 5);//금
				dayList.put("i6", 6);//토
				dayList.put("i7", 0);//일
		

				List<Integer> newDate = new ArrayList<>();

				if(date.length() < 3){
					newDate.add(dayList.get(date));
				}else {
					String[] sliceDate = date.split(",");
					for(int i=0; i < sliceDate.length; i++) {
						newDate.add(dayList.get(sliceDate[i]));
					}
				}
				return newDate;
	}
	
	/* 해당 의사의 날짜마다의 예약리스트 보여줌(모든 예약시 이미 예약된 항목 표시)
	 * @Param ReservationVO reservationVo = 의사번호, 예약년, 예약월, 예약일
	 * return List<ReservationVO>형태의 예약시간 리스트
	 */
	@PostMapping("/reserveList")
	@ResponseBody
	public List<ReservationVO> selectReserveList(@RequestBody ReservationVO reservationVo) {
		//선택한 의사의 해당날짜 예약시간 보여주기
		List<ReservationVO> reserveTimeList = mainService.findreserveListToChoice(reservationVo);
		return reserveTimeList;
	}
	
	/* 비대면예약페이지(select)
	 * @Param HttpServletRequest request = 세션에 담긴 유저아이디
	 * @Param String hospitalId = 병원아이디
	 * @Model model = 병원이름, 병원아이디, 의사목록, 병원휴무일
	 * return "user/untactReserve"
	 */
	@GetMapping("/untactReserve")
	public String unntactReservationPage(HttpServletRequest request, String hospitalId, Model model, ReservationVO reservationVo ) {
		//병원정보
		HospitalVO hosInfo = mainService.getHos(hospitalId);
		//병원이름&아이디
		model.addAttribute("hosName", hosInfo.getHospitalName());
		model.addAttribute("hospitalId", hospitalId);
		//병원의 의사정보
		List<DoctorVO> docList = hospitalService.getDoctorAll(hospitalId); 
		model.addAttribute("docList", docList);
		
		//세션으로 유저아이디 가져옴
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");
		model.addAttribute("userId", userId);
		
		//휴일 코드를 요일번호로 변경
		String date = hosInfo.getHoliday();		
		model.addAttribute("newDate", day(date));
		
		return "user/untactReserve";
	}
	
	
	/* 비대면예약페이지(insert)
	 * @Param ReservationVO reservationVo = 예약정보VO
	 * return boolean = 성공여부(true or false)반환
	 */
	@PostMapping("/untactReserve")
	@ResponseBody
	public boolean insertUntactReservation(@RequestBody ReservationVO reservationVo) {
		int result = mainService.insertUntactReservation(reservationVo);
		if(result == 0) { //insert 안되면 false
			return false;
		}else { //insert되면 true
			return true;
		}
	}
		
	/*
	 * @Param HttpServletRequest request = 세션에 담긴 유저아이디
	 * @Param String hospitalId = 병원아이디
	 * @Model model = 병원이름, 병원아이디, 의사목록, 병원휴무일
	 * return "user/untactAccept"
	 */
	//비대면실시간접수페이지(select)
	@GetMapping("/untactAccept")
	public String untactAcceptPage(HttpServletRequest request, String hospitalId, Model model) {
		//병원정보
		HospitalVO hosInfo = mainService.getHos(hospitalId);
		//병원이름&아이디
		model.addAttribute("hosName", hosInfo.getHospitalName());
		model.addAttribute("hospitalId", hospitalId);
		//병원의 의사정보
		List<DoctorVO> docList = hospitalService.getDoctorAll(hospitalId); 
		model.addAttribute("docList", docList);
		
		//세션으로 유저아이디 가져옴
		HttpSession session = request.getSession();
		String userId = (String) session.getAttribute("userId");
		model.addAttribute("userId", userId);
		
		//휴일 코드를 요일번호로 변경
		String date = hosInfo.getHoliday();
		model.addAttribute("newDate", day(date));
		return "user/untactAccept";
	}

	private String nextTime(int minute, int hour) {
		String canClinicNow ="";
		if(minute >= 30) {
    		canClinicNow = String.valueOf(hour + 1).concat(":00");
    	}else{
    		canClinicNow = String.valueOf(hour).concat(":30");
    	}
		return canClinicNow;
	}
	
	/* 비대면실시간접수페이지(대기현황)
	 * @Param DoctorVO doctorVO = 의사번호
	 * return Map<String,Object>형태의 대기인원, 지금당장 예약할 수 있는 가장빠른시간, 의사진료 시간지났는지 여부
	 */
	@PostMapping("/waitingList")
	@ResponseBody
	public Map<String,Object> findWaitingList(@RequestBody DoctorVO doctorVO) {
		List<ReservationVO> findWaitingList = mainService.findWaitingList(doctorVO);
		List<String> times = new ArrayList<>();//옛날 시간 넣을 곳
		List<String> newTimes = new ArrayList<>();//concat한 시간 넣을곳
		String canClinicNow = null; //지금당장 예약할 수 있는 가장 가까운 시간
		String firstReserve = null;//당일 첫번째 예약
		int firstReserveHour = 0;//첫번째 예약의 시
		boolean ClinicYN = true; //진료가능 여부
		int waitingPP = 0;
		
		 if(!findWaitingList.isEmpty()) { //전체 값을 제대로 받아왔다면
			//19:00와 같은 옛날시간 times배열에 넣음
			for(int i=0; i < findWaitingList.size(); i++) {
				times.add(findWaitingList.get(i).getReserveTime());
			}
			
			//1900와 같이 옛날시간 가공해서 newTimes배열에 넣음
			for(int i=0; i < times.size(); i++) {
				String hour = (times.get(i)).substring(0, 2);
				String minute = (times.get(i)).substring(3, 5);
				newTimes.add(hour.concat(minute));
			}
			firstReserve = newTimes.get(0); //가장빠른 예약시간
//			System.out.println("findWaitingList="+findWaitingList);
//			System.out.println("times ="+times);
//			System.out.println("newTimes ="+newTimes);
		 }
		 
		
		//ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ위는 시간계산
		
		//현재시간
		LocalDateTime now = LocalDateTime.now();
		
		// 현재 시
		int hour = now.getHour();
        // 현재 분
        int minute = now.getMinute();
        
        //의사진료 종료시간
        int doctorMaxtime = 0;
		
		
        if(!findWaitingList.isEmpty()) {
        	// 가장 빠른 예약시간 구하기
        	//firstReserve = Collections.max(newTimes);
	        for (int i = 0; i < newTimes.size(); i++) {
	            if (Integer.parseInt(newTimes.get(i)) < Integer.parseInt(firstReserve)) {
	                firstReserve = newTimes.get(i);
	                
	            }
	        }
	        //가장 빨리 예약할 수 있는 시간
	        firstReserveHour = Integer.parseInt(firstReserve.substring(0, 2));
        }
        
        
        
        //▶ 만약 대기하는 사람이 없으면
        if(times.isEmpty()) {
//			System.out.println("현재 대기하는 인원 없음");
			
			//의사 진료시간에 맞게 조건을 걸어야하지만 여
			canClinicNow = nextTime(minute, hour);
        
        //▶ 대기하는 사람이 있지만 지금 당장 예약할 수 있을때(현재시간 + 1시간한게 가장빠른 예약시간보다 더 빠르면)
        }else if(hour + 1  < firstReserveHour) {
//        	System.out.println("현재 대기 인원은 있지만 내 앞엔 없음");
        	canClinicNow = nextTime(minute, hour);
        	
        //▶ 대기하는 사람도 있고 지금당장 예약할 수 없을떄
        }else {
        	
        	// ▶ 예약된게 1개밖에 없고 그게 바로 내 앞임(내가 예약할 수 있는 시간 앞)
        	if(newTimes.size() == 1){
				//System.out.println("현재 내 앞 대기인원 1명");
				doctorMaxtime = Integer.parseInt(findWaitingList.get(0).getDoctorMaxtime());//의사 진료종료시간
				int sliceHour = Integer.parseInt((newTimes.get(0)).substring(0, 2));
				int sliceMinute = Integer.parseInt((newTimes.get(0)).substring(2, 4));
				
				//의사 진료시간안에 있고 끝이 30분으로 끝날때랑
				if(sliceMinute >=30 && doctorMaxtime > hour) {
					canClinicNow = String.valueOf(sliceHour + 1).concat(":00"); //현재 예약가능한 시간
				//00으로 끝날때
				}else if(doctorMaxtime > hour) {
					canClinicNow = String.valueOf(sliceHour).concat(":30"); //현재 예약가능한 시간
				}else {
					ClinicYN = false;//의사 진료시간이 넘어서 예약불가
				}
				waitingPP= 1;//기다리는 인원
        	
			// ▶ 예약많고 빈예약자리 찾아야함
			}else if(newTimes.size() > 1) {
				doctorMaxtime = Integer.parseInt(findWaitingList.get(0).getDoctorMaxtime());//의사 진료종료시간
				
				//1. 빈 예약찾는 연산
				for(int i= 0; i < (newTimes.size()-1) ; i++) {
					int extraNum = Integer.parseInt(newTimes.get(i + 1)) - Integer.parseInt(newTimes.get(i));
					
					if(extraNum >= 100 ) { //1.연산값이 100이상일때 = 중간에 빈예약이 있다는 뜻
						//System.out.println("현재 대기인원 여러명임");
						//2.빈예약 앞전 시간을 가져와서 시간과 분으로 쪼갬
						int sliceHour = Integer.parseInt((newTimes.get(i)).substring(0, 2));
						int sliceMinute = Integer.parseInt((newTimes.get(i)).substring(2, 4));
						
						//3. 예약가능한 시간 바로 앞 예약된 시간을 토대로 예약가능한 시간으로 계산해줄거임
						// ▶ 내 앞전에 예약이 있어서 내가 예약할 수 있는 가장 빠른시간을 찾을때
						if(canClinicNow == null) {//가장빠른 예약시간으로
							if(sliceMinute >=30 && doctorMaxtime > hour) {
								canClinicNow = String.valueOf(sliceHour + 1).concat(":00"); //현재 예약가능한 시간
							}else if(doctorMaxtime > hour) {
								canClinicNow = String.valueOf(sliceHour).concat(":30"); //현재 예약가능한 시간
							}else {
								ClinicYN = false;//의사 진료시간이 넘어서 예약불가
							}
							waitingPP= i;//기다리는 인원
						}
						
					}else {//▶ 중간에 빈 예약이 없고 지금 내가 마지막 예약임
						//바로 앞전 예약시간 자르기
						//System.out.println("현재 대기인원 여러명이고 내가 마지막차례임");
						int sliceHour = Integer.parseInt((newTimes.get(newTimes.size()-1)).substring(0, 2));
						int sliceMinute = Integer.parseInt((newTimes.get(newTimes.size()-1)).substring(2, 4));
						
						if(sliceMinute >=30 && doctorMaxtime > hour) {
							canClinicNow = String.valueOf(sliceHour + 1).concat(":00"); //현재 예약가능한 시간
						}else if(doctorMaxtime > hour) {
							canClinicNow = String.valueOf(sliceHour).concat(":30"); //현재 예약가능한 시간
						}else {
							ClinicYN = false;//의사 진료시간이 넘어서 예약불가
						}
						waitingPP= newTimes.size();//기다리는 인원
					}
				}
			}
        }
        
        
        if(doctorMaxtime < hour) { //저장전 한번더 넣기 / 진료시간 끝나면 예약못함
        	ClinicYN = false;
        }
        
        
        Map<String,Object> response = new HashMap<>();
        response.put("waitingPP",waitingPP);
        response.put("canClinicNow", canClinicNow);
        response.put("ClinicYN", ClinicYN);
        
//        System.out.println("대기 인원 ="+waitingPP);
//        System.out.println("지금당장 예약할 수 있는 시간 ="+canClinicNow);
//        System.out.println("의사진료시간이 지나지 않음 ="+ClinicYN);
        
		return response; 
	}
		
	/*유저 QNA 작성폼*/
	   @GetMapping("/qnaUserForm")
	   public String insertQnaMemForm(String hospitalId) {
	       return "user/qnaUserForm";
	   }
	   
	/*유저 QNA insert*/
	   @PostMapping("/qnaUserForm")
	   public void insertQnaMemProcess(
	                           @ModelAttribute QnaVO vo,
	                           HttpServletResponse resp,
	                           HttpServletRequest request) {

	      resp.setContentType("text/html; charset=UTF-8");
	      HttpSession session = request.getSession();
	      String userId = (String) session.getAttribute("userId");
	      String hosId = (String) session.getAttribute("hosId");
	      
	      vo.setHospitalId(hosId);
	      vo.setUserId(userId);
	      
	      if (vo.getUploadFiles()[0].isEmpty()) {
	         hospitalService.insertQnaMem(vo);
	         
	      }else {   
	         
	         hospitalService.insertQnaMem(vo);
	         
	         List<String> fileNames = fileUploadService.uploadFiles(vo.getUploadFiles());

	         // 파일정보
	         HashMap<String, Object> map = new HashMap<String, Object>();
	         map.put("chkList", fileNames);

	         for (String name : fileNames) {
	            vo.setFileName(name);
	            hospitalService.insertAttachQnaAns(vo);
	         }
	         
	      }
	      PrintWriter out;
	      try {
	         out = resp.getWriter();
	         out.println("<script language='javascript'>");
	         out.println("alert('등록을 성공했습니다.')");
	         out.println("window.location.href = '/hospitalDetail?hospitalId=" + hosId + "';");
	         out.println("</script>");
	         out.flush();
	      } catch (IOException e) {
	         e.printStackTrace();
	      }

	   }

}
