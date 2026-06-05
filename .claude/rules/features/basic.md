---
paths:
  - "**/*Message*.{java,xml}"
  - "**/*MfaUser*.{java,xml}"
  - "**/templates/basic/**"
  - "**/static/js/basic/**"
---
# 기본메뉴 (/basic)

## 메세지조회 (`/basic/message`)
- 테이블: `TB_MESSAGE`. 조회 전용, 페이징.
- 화면 템플릿: `templates/basic/message-manage.html`.
- 데이터 URL: `GET /basic/message/data`.
- `TB_MESSAGE m LEFT JOIN TB_DEPT d ON m.DEPT_ID = d.DEPT_ID` 기준으로 부서명을 조회한다.
- 실제 레거시 DB 컬럼명이 다르면 Java 필드명은 유지하고 MyBatis SQL alias로 맞춘다.

## MFA 사용자 관리 (`/basic/mfa`)
- 테이블: `TB_EMP` (MFA_YN, 사용자그룹 컬럼).
- TODO: `TB_EMP.MFA_YN` 컬럼 추가 전까지 해당 필드 구현 보류. 맵핑리스트 #2 참조.
- 조회 조건: 사용자그룹명(콤보), 권한(콤보), 사용자명, 사용자ID.
