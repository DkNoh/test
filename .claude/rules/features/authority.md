---
paths:
  - "**/*Authority*.{java,xml}"
  - "**/templates/**/authority*.html"
---
# 권한 (/authority/manage)

- 시스템 권한 코드 등록 / 삭제, 메뉴별 접근 권한 매핑.
- 연관: `MenuAuthInterceptor`, `TB_MENU_AUTH`. EMP 권한 플래그(PERM_SYS/CPN/STA/PSN/AUT/MMS, EMP_LEV=9 관리자).
- 권한 변경은 세션/인가에 즉시 영향 → 변경 후 동작 검증.
