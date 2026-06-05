---
paths:
  - "**/*SmsCustomer*.{java,xml}"
  - "**/*SmsSsn*.{java,xml}"
  - "**/templates/sms/customer*.html"
  - "**/templates/sms/ssn*.html"
---
# SMS 발송조회 확장 (/sms/customer, /sms/ssn)

## 고객별 조회 (`/sms/customer`)
- 테이블: `TB_SMS_HISTORY` + 예금주명·중개업체 JOIN (TODO).
- 수신번호·메세지 포함 → `@PrivacyLog` 필수 (`getData`에 이미 부착).
- 조회 조건: 메세지구분(라디오: SMS/MMS/본인인증/카카오), 휴대폰번호, 주민등록번호, 조회일자.
- VO: `SmsCustomerSearchVO`. 개인정보 표시 시 `MaskingUtil`.

## 주민번호 조회 (`/sms/ssn`)
- 주민번호(식별자) 직접 노출 — 최고 민감 등급. `@PrivacyLog` 필수.
- TODO: 식별자 연동 테이블 미확정 — 맵핑리스트 #5 참조. 확정 전 구현 금지.
- 현재 `SmsCustomerSsnMapper`를 컨트롤러에서 직접 호출(Service 계층 없음). 로직 추가 시 Service 추출.
- 조회 조건: 휴대폰번호(`receiverNo`). 단순 입력 1개이므로 DTO 불필요 — `@RequestParam` 허용 예외.
