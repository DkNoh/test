---
paths:
  - "**/*SmsHistory*.{java,xml}"
  - "**/templates/sms/history*.html"
  - "**/static/js/sms/history*.js"
---
# SMS 발송이력 (/sms/history)

- 화면: 목록(검색+페이징) / 상세 / 등록 / 삭제.
- 유형: SMS·LMS·ALIMTALK / 상태: WAIT·SUCCESS·FAIL / 재발송 `resend_yn`.
- 테이블: `TB_SMS_HISTORY` (PK = 시퀀스 selectKey).
- API: GET `/sms/history`(화면), GET `/sms/history/data`, POST `/sms/history/save`·`/delete`.
- 전화번호·본문 조회 시 `@PrivacyLog` + `MaskingUtil`.
- 검증: 수신번호 형식, 본문 길이(SMS 90byte / LMS 2000byte).
