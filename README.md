# 📌 itPlace – 통신사 멤버십 제휴처 안내 지도 서비스 플랫폼

<p align="center">
  <em>통신사 멤버십 혜택을 한눈에!</em><br>
  제휴처 위치 안내부터 AI 추천, 통계 관리까지 한 번에 제공하는 플랫폼
</p>

---

## 🚀 주요 기능

#### 👤 회원가입/로그인
- ✅ 일반 회원가입 및 소셜 로그인 지원  
- ✅ 유플러스 회원 연동
- ✅ 개인정보 관리  

---

#### 🗺️ 지도 및 위치 기반 시스템
- ✅ Kakao Map API를 활용한 제휴처 위치 표시
- ✅ GPS 기반 현재 위치 확인  
- ✅ 거리별·카테고리별 제휴처 필터링  
- ✅ 사용자 지정 위치에서 재검색  
- ✅ 제휴처 홈페이지 리다이렉트
- ✅ 제휴처 자체 DB 기반 검색  
- ✅ 길찾기
- ✅ 로드뷰 오버레이 지원

---

#### 🏪 제휴처 정보 제공
- ✅ 제휴처 상세 정보 조회  
- ✅ 자체 제휴처 DB 기반 정보 제공  
- ✅ 즐겨찾기 및 방문 이력 관리  

---

#### 🤖 제휴처 AI 추천 시스템
- ✅ 사용자 행동 로그 기반
- ✅ 사용자 위치 기반
- ✅ 카테고리별
  
---

#### 📊 관리자 통계 관리 시스템
- ✅ 제휴처별 이용 통계 제공  
- ✅ 사용자 행동 패턴 분석 대시보드  
- ✅ 혜택 이용률 모니터링  
- ✅ 멤버십 혜택 정보 관리  


---

## 👥 개발팀 소개

<p align="center">
<table>
  <tr>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/173465217?v=4" width="100" alt="허승현"/><br>
      <strong>허승현</strong><br><sub>팀장 · AI 추천</sub><br>
      <a href="https://github.com/HSH-11"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white"></a>
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/50799519?v=4" width="100" alt="이희용"/><br>
      <strong>이희용</strong><br><sub>보안·인증</sub><br>
      <a href="https://github.com/eddie-backdev"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white"></a>
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/135448774?v=4" width="100" alt="정현경"/><br>
      <strong>정현경</strong><br><sub>제휴처 시스템</sub><br>
      <a href="https://github.com/hyunnk"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white"></a>
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/130213698?v=4" width="100" alt="하령경"/><br>
      <strong>하령경</strong><br><sub>관리자 대시보드</sub><br>
      <a href="https://github.com/rxgx424"><img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white"></a>
    </td>
  </tr>
</table>
</p>

---

## 🛠️ 기술 스택

<p align="center">
  <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Java&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=Spring%20Security&logoColor=white"><br>
  <img src="https://img.shields.io/badge/OAuth-000000?style=for-the-badge&logo=OAuth&logoColor=white">
  <img src="https://img.shields.io/badge/ChatGPT%20API-10A37F?style=for-the-badge&logo=openai&logoColor=white"><br>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white">
  <img src="https://img.shields.io/badge/Kakao%20Map-FFCD00?style=for-the-badge&logo=Kakao&logoColor=black">
</p>

---

## 📁 프로젝트 구조

```text
📦 itplace-user-api
├─ src/main/java/com/itplace/userapi
│  ├─ benefit       # 혜택 관리
│  ├─ common        # 공통 유틸/DTO
│  ├─ partner       # 제휴처 정보
│  ├─ security      # 인증·권한
|  ├─ map           # 지도 
│  ├─ user          # 사용자 관리
│  └─ favorite      # 즐겨찾기
├─ src/main/resources
├─ docs            # 프로젝트 문서
└─ README.md
