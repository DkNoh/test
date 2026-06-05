---
paths:
  - "**/*Approval*.{java,xml}"
  - "**/templates/approval/**"
  - "**/static/js/approval/**"
---
# 결재 (/approval)

- 발송 전 결재 요청 / 승인 / 반려. 상태: PENDING·APPROVED·REJECTED.
- 일괄(배치) 승인 지원.
- 권한: `PERM_AUT`. 결재 액션에 `@PrivacyLog` 고려.
