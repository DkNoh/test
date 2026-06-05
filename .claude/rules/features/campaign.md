---
paths:
  - "**/*Campaign*.{java,xml}"
  - "**/templates/**/campaign*.html"
  - "**/static/js/**/campaign*.js"
---
# 캠페인 (/campaign, /sms/campaign)

## 발송 대상 관리 (`/campaign/target-manage`)
- 테이블: `TB_CAMPAIGN`. VO: `CampaignVO`, 검색: `CampaignSearchVO`.
- 네이밍: 검색 VO는 `CampaignSearchVO` (소문자 시작 `campaignSearchVO` 금지 — 2026-06-02 교정됨).
- Two-Track + `ApiResponse<PageResponseDTO<CampaignVO>>`.
- TODO: 등록자·대상수·승인자 연동 미완성 — 맵핑리스트 #6 참조.

## 발송 등록 / 승인 (`/campaign/{sendType}/register|approve`)
- 컨트롤러: `CampaignSendController`. `{sendType}` = `sms` | `lms` | `alimtalk`.
- 등록(`POST /{sendType}/submit`): `vo.setSendType(sendType.toUpperCase())` 후 `CampaignTargetService.register()`.
- 승인 대기 목록(`GET /{sendType}/pending`): sendType 대문자 고정.
- 승인 처리(`POST /approve`): action = `APPROVED` | `REJECTED`. `SecurityContextHolder`로 현재 사용자 취득.
- 현재 등록·승인 화면은 stub — 로직 설계 후 구현 예정.

## 발송내역 조회 (`/sms/campaign`, `/sms/campaign-lms`, `/sms/campaign-alimtalk`)
- 세 컨트롤러 모두 `CampaignHistoryService` + `CampaignHistoryVO` 공유.
- `SEND_TYPE` 값으로 구분: 컨트롤러에서 `request.setSendType(...)` 후 서비스 호출.

| URL | 컨트롤러 | SEND_TYPE |
|-----|----------|-----------|
| `/sms/campaign` | `CampaignController` / `campaignSearchController` | SMS |
| `/sms/campaign-lms` | `CampaignLmsHistoryController` | `LMS` |
| `/sms/campaign-alimtalk` | `CampaignAlimtalkHistoryController` | `ALIMTALK` |

- TODO: `TB_CAMPAIGN` ↔ `TB_SMS_HISTORY` 연결키 미확정 — 맵핑리스트 #14~16 참조.
