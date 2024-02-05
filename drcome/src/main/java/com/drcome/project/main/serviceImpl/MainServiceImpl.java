<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
	xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
	layout:decorate="~{layouts/layout}">

<head>
<meta charset="UTF-8">
<title>Search</title>
<script type="text/javascript"
	th:src="@{//dapi.kakao.com/v2/maps/sdk.js?appkey=7be6b1f8b5b642b5d341f732b8dc384e&libraries=services}"></script>
<script
	src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>

<style>
.head_info {
	display: flex;
	justify-content: space-between;
	margin-top: 30px;
}

.search {
	display: flex;
	justify-content: flex-end;
	margin-bottom: 40px;
}

button {
	border: none;
	font-weight: bold;
	font-size: 18px;
	background-color:#06BBCC;
	color:white;
}

button:hover {
	color: black;
}

.ho:hover {
	background-color: #B0E0E6;
	cursor: pointer;
}
</style>
</head>

<body>
	<div layout:fragment="content" style="margin-bottom:200px;">
		<div class="container" style="margin-top: 50px; text-align: center;">
			<div class="head"
				style="display: flex; justify-content: center; margin-bottom: 130px;">
				<div style="width: 600px;">
					<h2
						style="margin-bottom: 20px; color: #363636;">내 근처 추천 약국</h2>
					<hr>
					<hr>
					<h5 class="loc" style="color: gray;">현재위치들어가는 곳</h5>
					<button class="btn btn-warning rounded-pill px-3" type="button"
						onclick="switchLocation()">현재위치변경</button>
				</div>
			</div>
			<script th:inline="javascript">
				function nowLocation(){
					const loc = window.localStorage.getItem("location");
					//console.log(loc);
					document.querySelector(".loc").innerText='현재 내 위치는 ' + loc + ' 입니다.';
					
				}
				nowLocation();
			</script>


			<!-- 약국리스트 -->
			<div style="margin-bottom: 200px;">
				<h6 style="text-align:left;" th:text="|현재 진료 번호 :|"></h6><h6 class="clinic" th:text="${clinic}"></h6>
				<table class="table">
					<thead>
						<tr>
							<th><button type="button" onclick="insertPhaSelect()" >약 처방 신청하기</button></th>
							<th>약국명</th>
							<th>전화번호</th>
							<th>운영시간</th>
							<th style="width: 330px;">주소</th>
							<th class="lo">거리</th>
						</tr>
					</thead>
					<tbody class="here">
						<th:block th:each="pha : ${phaList}">
							<tr th:if="${#lists.size(phaList) > 0}" class="ho hospitalItem" th:data="${pha.pharmacyId}">
								<td><input type="checkbox" class="checkbox" /></td>
								<td th:text="${pha.pharmacyName}" th:onclick="|location.href='@{pharmacyDetail(pharmacyId=${pha.pharmacyId})}'|"></td>
								<td th:text="${pha.pharmacyPhone}"></td>
								<td th:text="|${pha.opentime} ~ ${pha.closetime}|"></td>
								<td th:text="${pha.address}" th:onclick="|location.href='@{pharmacyDetail(pharmacyId=${pha.pharmacyId})}'|"></td>
								<td style="font-weight: bold;" class="address"
									th:text="${pha.address}"></td>
							
							</tr>
						</th:block>
						<tr th:unless="${#lists.size(phaList) > 0}">
							<td colspan="5"
								style="color: gray; height: 100px; vertical-align: middle;">검색
								조건에 맞는 약국이 없습니다.</td>
						</tr>
					</tbody>
				</table>
			</div>

			<script th:inline="javascript">
			// 내 현재 주소를 daum 주소api로 바꾸기(daum주소 api)
	         function switchLocation(){
	                new daum.Postcode({
	                    oncomplete: function(data) {
	                       //console.log(data.address);
	                       document.querySelector(".loc").innerText='현재 내 위치는 ' + data.address + ' 입니다.';
	                       //로컬스토리지에 정보 덮어씀
	                       window.localStorage.setItem('location',data.address);
	                       
	                       <!--4. 바뀐 내 주소로 새 좌표찾기(kakao지도 api라이브러리) -->
	                       var geocoder = new kakao.maps.services.Geocoder();
	      
	                       var callback = function(result, status) {
	                           if (status === kakao.maps.services.Status.OK) {
	                              let newLat = result[0].address.y;
	                              let newLon = result[0].address.x;
	                              //console.log('바뀐주소Lat = ',result[0].address.y);
	                               //console.log('바뀐주소Lon = ',result[0].address.x);
	                               //로컬스토리지에 정보 덮어씀
	                               window.localStorage.setItem('lat',result[0].address.y);
	                           window.localStorage.setItem('lon',result[0].address.x);
	                           window.location.reload();
	                           }     
	                       };
	                       geocoder.addressSearch(data.address, callback);
	                    }
	                }).open(); 
	             
	         }
			
			
			//현재의 좌표와 뽑아낸 좌표로 거리계산하기(지도와 지도의 직선거리임 가장 가까운 경로거리 아님)
	    //5. 병원주소를 좌표로 바꿈
	    async function restart(){
		   	 var geocoder = new kakao.maps.services.Geocoder();
		   	 
		   	let locs = document.querySelectorAll('.lo');

		   	 
		   	 //병원
		   	 let address = document.querySelectorAll('.address');

		   	 //console.log("address=",address);
		   	 if(!address){
		   		 //console.log("강제종료");
		   		 return;
		   	 }
		   	 
		   	 
		   	 let hospitalList = [];
		        
		    	 function promiseFn(item, index){// 인덱스, 계산한 거리, 조건에 맞는 상위요소와 함께 배열에 넣는 함수
		    		//console.log("item= ",item);
		    		 return new Promise((resolve)=>{
		    			 geocoder.addressSearch(item.innerHTML, (result, status)=>{ //item.innerHTML= 주소 그 자체
		    				if (status === kakao.maps.services.Status.OK) {
		    					//console.log("카카오맵에서 찾았음")
		                       //병원 좌표
		                       let hosLat = result[0].address.y;
		                       let hosLon = result[0].address.x;
		                       //console.log('병원Lat = ',result[0].address.y);
		                       //console.log('병원Lon = ',result[0].address.x);
		                       
		                     	//현재위치 좌표
		                       let curLat = window.localStorage.getItem('lat');
		                       let curLon = window.localStorage.getItem('lon');
		                       //console.log('세션Lat = ',curLat);
		                       //console.log('세션Lon = ',curLon);
		                       
		                       //거리계산된 값
		                       let dist = (distance(curLat, curLon, hosLat, hosLon)).toFixed(2);
		                       //console.log("총 거리 =", dist);
		                       item.innerText = dist + 'km';
		                       
		                       if((item.closest('.hospitalItem')) != null){
		                           hospitalList.push({
		                           	'index' : index,
		                           	'dist' : dist,
		                           	'parent_node' : item.closest('.hospitalItem') //저 클래스가 있는 item의 상위요소
		                           });
		                       }else if((item.closest('.pharmacyItem')) != null){
		                           pharmacyList.push({
		                           	'index' : index,
		                           	'dist' : dist,
		                           	'parent_node' : item.closest('.pharmacyItem') //저 클래스가 있는 item의 상위요소
		                           });
		                       }
		                       resolve(); //여기까지 도달하면 성공했다는 뜻
		                       
		    			 	}else{
		    			 		//console.log("restart함수실행안됨");
		    			 		reject()
		    			 	}         				
		    		 	});
		    	 	});       
		        }  
		    	 //병원
		    	 //address(태그자체)로 만든 배열을 새로운 배열로 바꿀건데,
		    	 //그 새로운 배열map은 promiseFn함수에 address배열에 있는 
		    	 //item값(태그와 내용이 있음/ 내용만 뽑으려면 item.innerText)과 index를 넣어 만들것이다.
		        const promises = Array.from(address).map((item, index)=> promiseFn(item, index));
		        //console.log("promises=",promises)
		        await Promise.all(promises); //모든 promise들이 성공을 return 할때까지 잠시 기다리겠다.
				 
		        //거리 짧은 순으로 정렬 sort((a, b) => a - b) = 오름차순
		        //parseFloat()=문자열을 실수로 변경
		        hospitalList.sort((a, b)=> parseFloat(a.dist) - parseFloat(b.dist));
		        //console.log("hospitalList=", hospitalList);
		        
		        //hospitalItem클래스를 가진 div전체(하위요소 포함)
		        const hospitalListBox = document.querySelector('.here');
		        
		        //console.log("hospitalListBox=", hospitalListBox); 
		        
		        //div전체에 부모 노드(똑같은div 근데 이제 거리순으로 정렬된)를 넣어줌
		        hospitalList.forEach(({parent_node}) =>{
		       	 hospitalListBox.append(parent_node);
		        })
		        
		    }; //restart함수 끝
		       
		       
		    //6. 본격 좌표로 거리계산 시작
		      //Math.PI 는 파이값임(3.14...)
		      //Math.sin()은 라디안으로 주어진 각도의 사인 값인 -1과 1 사이의 수를 반환 | sin = 높이 / 빗변
		      //Math.cos는 라디안으로 주어진 각도의 코사인 값을 반환합니다 | cos = 밑변 / 빗변
		      //Math.tan()은 각도의 탄젠트 값을 표현하는 수를 반환 | tan = 높이 / 밑변
		      //Math.Atan2(Double, Double)는 탄젠트를 적용했을 때 지정된 두 숫자의 몫이 나오는 각도를 반환
		      //Math.Sqrt()는 double타입의 인수를 전달하면 인수에 대한 double타입의 제곱근 값을 리턴
		
		      // 각도를 라디안(1라디안 = 57.3도)으로 변환하는 함수
		      //라디안 값 = 각도 x (파이 / 180 )
		      function radian( i ){
		          return i * (Math.PI / 180)
		      }
		
		      //거리 구하는 함수
		      function distance(curLat, curLon, hosLat, hosLon){ //현재좌표 - 병원좌표
		          const earth_r = 6371 // 지구의 반지름(km)
		          const lat = radian( hosLat - curLat ) //위도끼리 뺀 값
		          //console.log("위도 뺀 값=", lat)
		          const lon = radian( hosLon - curLon ) // 경도끼리 뺀 값
		          //console.log("경도 뺀 값=", lon)
		          
		          const a = Math.sin(lat/2) * Math.sin(lat/2) + //위도
		          Math.cos(radian(curLat)) * Math.cos(radian(hosLat)) * // cos
		          Math.sin(lon/2) * Math.sin(lon/2); //경도
		          //console.log("a =", a);
		
		          const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		          //console.log("c 2개의 각도 =", c);
		          const distance = earth_r * c // 반지름 x 2개의 각도값
		
		          return distance;
		      }
		      restart();
		      
		      function insertPhaSelect(e){
		    	let checkboxes =  document.querySelectorAll(".checkbox");
		    	let clinicNo = document.querySelector(".clinic").innerText;
		    	console.log("checkboxes=",checkboxes);
		    	let pharmacyId = [];
		    	checkboxes.forEach((ele)=>{
		    		if(ele.checked == true){
		    			let tr = ele.closest('tr');
		    			pharmacyId.push(tr.getAttribute('data'));
		    		}
		    	});
		    	console.log("pharmacyId=",pharmacyId);
	    		console.log("clinicNo=", clinicNo);
	    		
	    		let param = {
		      			"clinicNo" : clinicNo,
		      			"pharmacyId" :pharmacyId
		      		}
	    		
	    		$.ajax("/recommendPharmacy",{
	    			type:"post",
	    			contentType: "application/json",
	    			data : JSON.stringify({
	    				"clinicNo" : clinicNo,
		      			"pharmacyId" :pharmacyId
	    			}),
	    			
	   				 // 요청이 성공한 경우 실행되는 함수
	   			    success: function(response) {
	   			        console.log("서버 응답:", response);
	   			    },
	
	   			    // 요청이 실패한 경우 실행되는 함수
	   			    error: function(jqXHR, textStatus, errorThrown) {
	   			        console.error("에러 발생:", textStatus, errorThrown);
	   			    }
	    		});
	    		
	    		
		      }
		      
		      	
		
			  
		</script>
		</div>
	</div>

</body>
</html>