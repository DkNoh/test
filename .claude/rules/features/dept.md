---
paths:
  - "**/*Dept*.{java,xml}"
  - "**/*Emp*.{java,xml}"
  - "**/templates/system/dept*.html"
---
# 부서 / 사원 (/system/dept)

- 부서 `TB_DEP`, 사원 `TB_EMP` CRUD. (`TB_` 컨벤션 통일, 스키마 한정자 제거)
- 사원은 로그인 계정 + 권한 플래그 보유 → 개인정보 `@PrivacyLog`.
- 비밀번호는 BCrypt로 암호화 저장.
