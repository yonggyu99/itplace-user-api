# 유레카 융합 프로젝트 - itPlace

# 📌 통신사 멤버십 제휴처 안내 지도 서비스 플랫폼

> **통신사 멤버십 혜택을 한눈에!**  
> 제휴처 위치 안내부터 추천, 통계 관리까지 한 번에 제공하는 플랫폼

---

## 🚀 주요 기능

### 1. 👤 회원가입/로그인
- ✅ 일반 회원가입 및 소셜 로그인 지원
- ✅ 멤버십 등급별 권한 관리
- ✅ 개인정보 관리 및 프로필 설정

---

### 2. 🗺️ 지도 및 위치 기반 시스템
- ✅ Kakao Map API를 활용한 제휴처 위치 표시
- ✅ GPS 기반 현재 위치 확인
- ✅ 거리별, 카테고리별 제휴처 필터링
- ✅ 사용자 지정 위치에서 재검색
- ✅ 제휴처 홈페이지 리다이렉트
- ✅ 길찾기(리다이렉팅)

---

### 3. 🏪 제휴처 정보 제공
- ✅ 제휴처 상세 정보 조회
- ✅ 자체 DB 구축
- ✅ 즐겨찾기 및 방문 이력 관리

---

### 4. 🤖 제휴처 추천 시스템
- ✅ 사용자 행동 기반 근처 AI 추천
- ✅ 카테고리별 추천 (음식, 쇼핑 등)

---

### 5. 📊 관리자 통계 관리 시스템
- ✅ 제휴처별 이용 통계 제공
- ✅ 시스템 분석 대시보드
- ✅ 혜택 이용률 모니터링
- ✅ 멤버십 혜택 정보 관리

---

## 👥 개발팀 소개

<table align="center">
  <tr>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/173465217?v=4" width="150px" alt="팀원1"/>
      <br>
      <b>허승현</b>
      <br>
      <sub>팀장 | 제휴처 AI 추천 시스템</sub>
      <br>
      <a href="https://github.com/HSH-11">
        <img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white">
      </a>
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/50799519?v=4" width="150px" alt="팀원2"/>
      <br>
      <b>이희용</b>
      <br>
      <sub>인증/권한 관리</sub>
      <br>
      <a href="https://github.com/eddie-backdev">
        <img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white">
      </a>
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/135448774?v=4" width="150px" alt="팀원3"/>
      <br>
      <b>정현경</b>
      <br>
      <sub>제휴처 제공 시스템</sub>
      <br>
      <a href="https://github.com/hyunnk">
        <img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white">
      </a>
    </td>
    <td align="center">
      <img src="https://avatars.githubusercontent.com/u/130213698?v=4" width="150px" alt="팀원4"/>
      <br>
      <b>하령경</b>
      <br>
      <sub>관리자 시스템(통계, 로그)</sub>
      <br>
      <a href="https://github.com/rxgx424">
        <img src="https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=GitHub&logoColor=white">
      </a>
    </td>
  </tr>
</table>

---

## 🛠️ 기술 스택

<div align="center">
  <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Java&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=Spring%20Boot&logoColor=white">
  <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=Spring%20Security&logoColor=white">
  <img src="https://img.shields.io/badge/OAuth-000000?style=for-the-badge&logo=OAuth&logoColor=white"><br>
  <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white">
  <img src="https://img.shields.io/badge/ChatGPT%20API-10A37F?style=for-the-badge&logo=openai&logoColor=white">
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
</div>


---

## 📁 프로젝트 구조

```
📦 itplace-user-api
├── 📂 src/main/java/com/itplace/userapi
│   ├── 📂 benefit       # 혜택 관리
│   ├── 📂 common        # 공통 모듈
│   ├── 📂 partner       # 제휴처 정보 관리
│   ├── 📂 security      # 인증/권한 관리
│   ├── 📂 user          # 사용자 관리
│   └── 📂 favorite      # 즐겨찾기 관리
├── 📂 src/main/resources
├── 📂 docs              # 문서
└── 📄 README.md
```

---

## 📊 API 문서

<div align="center">
  <a href="http://localhost:8080/swagger-ui.html">
    <img src="https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=black" alt="swagger">
  </a>
</div>

---

