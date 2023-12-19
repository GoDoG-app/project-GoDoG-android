# 반려가족과의 행복한 순간, GoDoG 🐕
<img src="https://github.com/GoDoG-app/project-GoDoG-android/assets/130967356/f1f1e9cd-2379-4126-9341-922151bbe83b">

<div align=center>
  <h2>사용한 개발 Tool💻</h2>  
  <img src="https://img.shields.io/badge/Python-3776AB?style=flat&logo=python&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java-F7DF1E?style=flat&logo=javascript&logoColor=white"/>
  <img src="https://img.shields.io/badge/AmazonAWS-232F3E?style=flat&logo=amazonaws&logoColor=white"/>
  <img src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat&logo=androidstudio&logoColor=white"/>
  <img src="https://img.shields.io/badge/Visual Studio Code-007ACC?style=flat&logo=visualstudiocode&logoColor=white"/>
  <br>
  <img src="https://img.shields.io/badge/Serverless-FD5750?style=flat&logo=serverless&logoColor=white"/>
  <img src="https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white"/>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=flat&logo=firebase&logoColor=white"/>
  <img src="https://img.shields.io/badge/Github-181717?style=flat&logo=github&logoColor=white"/>
</div>

<br>
<br>
<h2>🌐 바로가기</h2>

- Figma 화면기획서 : https://www.figma.com/proto/MJRqZZjh5cfzvLpQewLdMk/walk?type=design&node-id=204-582&t=WmC8c2gizHdNtQr6-1&scaling=scale-down&page-id=0%3A1&starting-point-node-id=204%3A582&show-proto-sidebar=1&mode=design
- ERD cloud : https://www.erdcloud.com/d/yJ74eC2KMiisGuhBi
- API 명세서 : https://documenter.getpostman.com/view/28003812/2s9Y5WxiSb
- 백엔드 GitHub Respository : https://github.com/kje0058/project-walking-app
- 프론트엔드 GitHub Respository : https://github.com/kje0058/project-walk-app-android

<br>
<h2>📅 프로젝트 기간</h2>

- 2023년 5월 ~ 9월 (5개월)

<br>
<h2>💡 프로젝트 기획 의도</h2>

한국인 4명 가운데 1명이 반려동물을 키우는 반려인구 1500만 시대가 열리며,

이에 따라 반려동물을 ‘우리 딸, 아들’ ‘막둥이’라고 칭하는 등 가족처럼 여기는 펫펨족을 겨냥한 시장도 성장하고 있습니다.

반려동물과 가장 많은 시간을 함께하는 산책을 색다르게 보내기 위해 다른 산책 서비스에는 없는 **"산책로 추천 앱"** 을 기획하게 되었습니다. 

<br>
<h2>🐕 프로젝트 소개</h2>

내 반려가족과 함께 하는 즐거운 산책!

채팅, 커뮤니티로 기능으로 친구와 소통이 가능하고, TMap 보행자 경로 API를 사용하여 주변의 산책로를 추천해주는 기능으로 색다른 산책을 해볼 수 있습니다.

<br>
<h2>📚 프로젝트 프론트엔드 사용 기술</h2>

- Android Studio(IDE) Java 언어를 사용하여 앱 개발
- Retrofit2 라이브러리로 데이터 파싱 및 REST API 통신
- 카카오 로그인 API 라이브러리를 이용하여 소셜 로그인 기능 구현
- TMap API 라이브러리를 이용하여 산책로 추천 및 실시간 산책 구현
- Firebase 의 Firestore 에 채팅 및 댓글 저장 및 관리
- RecyclerView 위젯과 Adapter 를 구현하여 많은 수의 데이터를 유연하게 표시 
- fragment 를 사용해서 레이아웃을 분리하여 효율적으로 관리
- 이미지 라이브러리 Glide를 사용하여 이미지를 효율적으로 관리

<br>
<h2>📌 주요 기능</h2>

<h4>1. 오늘의 산책로 추천</h4>
   
   **Tmap SDK의 POI 주변검색, 실시간 지도보기, 보행자경로안내 사용**

- 산책로 추천

  홈 화면에서 내 주변 공원을 랜덤으로 추천합니다. 그리고 산책로까지의 빠른 경로와 느린 경로를 보여주고 소요시간, 거리를 표시해줍니다.

- 추천산책로 산책하기

  지도 위치를 현재 내 위치로 잡아주고, 산책을 시작하면 시간과 거리가 작동합니다. 그리고 이동한 위치를 마커와 라인으로 표시해줍니다.

<details>
<summary>산책로 추천👆</summary>
<img src="https://github.com/GoDoG-app/project-GoDoG-android/assets/130967356/839e73f1-611f-4113-ac95-a8b95d334784">
</details>

<br>

<h4>2. 실시간 산책과 산책 기록</h4>
   
   **Tmap SDK의 POI 주변검색, 실시간 지도보기, 보행자경로안내 사용**

- 실시간 산책

  지도 위치를 현재 내 위치로 잡아주고, 산책을 시작하면 시간과 거리가 작동합니다. 그리고 이동한 위치를 마커와 라인으로 표시해줍니다.

- 산책기록
  
  산책 총 횟수, 거리, 시간을 표시하며, 하단에는 달력과 저장한 산책 기록 목록을 보여줍니다.

<details>
<summary>산책하기&기록👆</summary>
<img src="https://github.com/GoDoG-app/project-GoDoG-android/assets/130967356/9298bdba-de7f-4ac4-a281-a2325197021e">
</details>

<br>

<h4>3. 내 주변의 친구 추천</h4>

- 서버에서 Haversine 공식을 사용하여 두 지점 간의 거리를 계산하고, 사용자의 위치 반경 내에서 랜덤으로 위도와 경도를 생성합니다.

<details>
<summary>접기/펼치기👆</summary>
<img src="https://github.com/GoDoG-app/project-GoDoG-android/assets/130967356/6aaa927f-3c62-467c-8566-b8a773d1235a">
</details>

<br>


<h4>4. 커뮤니티</h4>

- 게시글 목록

  유저가 작성한 게시글을 확인할 수 있고, 하트 아이콘을 누르면 좋아요를 찍을 수 있습니다.

- 카테고리

  전체, 일상, 정보, 산책 카테고리로 나뉘어져 있고 각각의 카테고리별로 게시글을 확인할 수 있습니다.

- 게시글 상세보기 + 댓글

  게시글을 클릭하면 상세 화면으로 이동하고, 댓글 작성과 확인이 가능합니다.
  
  실시간으로 많은 양의 댓글 DB를 저장하기 위해 Firebase Firestore를 사용했습니다.

- 게시글 작성

  하단 + ProgressBar 버튼으로 글 작성이 가능합니다.

<details>
<summary>커뮤니티👆</summary>
<img src="https://github.com/GoDoG-app/project-GoDoG-android/assets/130967356/ad5e4c6f-21b7-4a05-949f-86ea8bff5f28">
</details>

<br>

<h4>5. 채팅 & 약속잡기</h4>

- 채팅목록

  내가 속한 채팅방 목록을 모두 보여줍니다.

- 1:1 채팅방

  내가 보낸 메세지와 친구가 보낸 채팅 메세지를 구별하여 채팅방에 표시해줍니다.

- 약속잡기

  날짜, 약속시간, 장소를 선택하여 친구와 산책약속을 정할 수 있으며, 약속이 잡히면 약속 메시지가 채팅창에 자동으로 전송되어 보여줍니다.
  
  실시간으로 많은 양의 채팅 DB를 저장하기 위해 Firebase Firestore를 사용했습니다.

- 약속장소

  카카오 우편번호 검색 자바스크립트를 활용하여 주소를 검색할 수 있습니다.

<details>
<summary>채팅&약속잡기👆</summary>
<img src="https://github.com/GoDoG-app/project-GoDoG-android/assets/130967356/e5ca63e5-d471-4bb1-b9c8-e6a3251614ad">
</details>

<br>

<h4>6. 내 정보</h4>

- 내 정보&반려가족 정보

  내 정보와 반려가족의 정보를 수정할 수 있고 산책 파트너 목록(친구), 산책 기록, 내가 쓴 커뮤니티 글과 댓글을 확인할 수도 있습니다.

<details>
<summary>내 정보👆</summary>
<img src="https://github.com/GoDoG-app/project-GoDoG-android/assets/130967356/3c206bc0-de44-4fc2-8507-fba8ec211c98">
</details>

<br>
<h2>📱 프로젝트 시연 동영상</h2>

https://drive.google.com/file/d/1ZpAn0eP5NPqrZfY4rBPOdqAR7lLDeX8q/view?resourcekey

<br>
<h2>🔥 에러사항</h2>
<details>
<summary>문제1. 워터풀 개발방식👆</summary>
  
- 백엔드에서 개발한 API를 중간에 한꺼번에 배포하다보니 어디서 에러가 발생한지 모르는 상황 발생
- 대처: 애자일 개발방식으로 변경
  
  서버를 새로 만들어 API를 하나씩 테스트하고 배포하는 애자일 방식으로 개발을 진행하여 에러를 찾았고,
  라이브러리를 설치할 때 자동 설치된 라이브러리의 버전 문제였고 버전을 낮춰 해결

<img src="https://github.com/GoDoG-app/project-GoDoG-android/assets/130967356/a7d735f2-03f5-494c-86ab-fc7dbad73be3">
</details>

<details>
<summary>문제2.  Git push, pull시 충돌에러👆</summary>

- 팀원 두명이 같은 파일을 수정해서 동시에 git에 올려 충돌 발생
- 대처 : Git push시 팀원간의 소통
  
  Git branch를 만들어 git pull, push 상황을 공유하고 충돌이 더이상 일어나지 않게 Slack에서 소통함
  Git Gragh를 확인하여 git push 상황을 체크함

<img src="https://github.com/GoDoG-app/project-GoDoG-android/assets/130967356/68cabf96-47c7-4b5d-b220-b15c71d692c3">
</details>

<br>
<h2>👨‍💻 팀원</h2>

|이름|깃허브|역할|
|------|---|---|
|김정은|https://github.com/kje0058| 친구 추천, 카카오 로그인 및Firebase 채팅 개발|
|김예진|https://github.com/blue618020| 안드로이드 기능 개발|
|최태욱|https://github.com/skdixodnr| 서버 기능 개발|
|황덕우|https://github.com/the9world| TMap 지도 및 산책관련 기능 개발|
