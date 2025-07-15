# 잇플레이스 사용자 API 서버입니다.
# 유레카 융합 프로젝트 - itPlace

## 프로젝트 개요
- 사용자의 통신 성향을 분석해 요금제 및 상품을 비교·추천하는 AI 챗봇 서비스를 개발했습니다.
- 챗봇은 멀티턴 대화와 금칙어 필터링 기능을 지원하며, 이를 통해 사용자에게 맞춤형 상담 경험을 제공합니다.

## 팀원 소개

| 이름   | 역할             | 주요 구현 내용                                 | GitHub                                                                                                                                                          |
|--------|------------------|----------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 이희용 | AI, 백엔드(팀장)  | 프롬프트 엔지니어링, AI 챗봇                   | <a href="https://github.com/eddie-backdev"><img src="https://avatars.githubusercontent.com/u/50799519?v=4" width="100" height="100" alt="eddie-backdev" /></a> |
| 박소연 | AI, 백엔드       | 리뷰 비즈니스 로직, AI 챗봇                    | <a href="https://github.com/so-yeon1"><img src="https://avatars.githubusercontent.com/u/82212460?v=4" width="100" height="100" alt="so-yeon1" /></a>           |
| 신혜원 | AI, 백엔드           | 요금제 비즈니스 로직, 요금제 추천 AI           | <a href="https://github.com/hyew0nn"><img src="https://avatars.githubusercontent.com/u/113279618?v=4" width="100" height="100" alt="hyew0nn" /></a>            |
| 이재윤 | AI, 백엔드       | 프롬프트 엔지니어링, AI 챗봇                   | <a href="https://github.com/iju42829"><img src="https://avatars.githubusercontent.com/u/116072376?v=4" width="100" height="100" alt="iju42829" /></a>          |
| 정동현 | 백엔드           | 서비스 인증 / 인가                            | <a href="https://github.com/Iamcalmdown"><img src="https://avatars.githubusercontent.com/u/144317474?v=4" width="100" height="100" alt="Iamcalmdown" /></a>     |
| 홍석준 | AI, 백엔드           | 요금제 비즈니스 로직, 리뷰 요약 AI             | <a href="https://github.com/seokjuun"><img src="https://avatars.githubusercontent.com/u/45346977?v=4" width="100" height="100" alt="seokjuun" /></a>           |

## 주요 기능
| 구분       | 기능 설명                                                                 |
|------------|-------------------------------------------------------------------------- |
| AI 챗봇    | 사용자 통신 성향 기반 요금제 추천, 멀티턴 대화 지원, 욕설 필터링, 프롬프트 해킹 차단 |
| 요금제     | 요금제 필터링 조회, 사용자 성향 기반 요금제 적합 여부 판단                     |
| 리뷰       | 리뷰 조회 및 등록, 좋아요 기능, 욕설 필터링, 리뷰 요약                        |
| 보안       | OAuth 및 Spring Security 기반 로그인/회원가입, 회원가입 시 이메일 인증, 비밀번호 찾기 기능 |
| 성향       | 성향 등록, 수정, 조회                                                       |