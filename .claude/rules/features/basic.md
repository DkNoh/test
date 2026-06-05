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
- TODO: TB_MESSAGE 쿼리 미완성 상태 — 맵핑리스트 #1 참조.

## MFA 사용자 관리 (`/basic/mfa`)
- 테이블: `TB_EMP` (MFA_YN, 사용자그룹 컬럼).
- TODO: `TB_EMP.MFA_YN` 컬럼 추가 전까지 해당 필드 구현 보류 — 맵핑리스트 #2 참조.
- 조회 조건: 사용자그룹명(콤보), 레벨(콤보), 사용자명, 사용자ID.
