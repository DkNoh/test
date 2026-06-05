---
paths:
  - "**/*Statistic*.{java,xml}"
  - "**/*Stat*.{java,xml}"
  - "**/templates/sms/stat*.html"
---
# 통계 (/sms/statistics, /sms/dept-stat)

- 마케팅 수신거부 통계, 부서별 발송 통계.
- 권한: `PERM_STA`. 집계 쿼리는 대량 `TB_SMS_HISTORY` 대상이므로 인덱스/성능 주의.
- 조회 전용 → `@Transactional(readOnly = true)`.
