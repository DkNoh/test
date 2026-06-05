---
paths:
  - "**/*Menu*.{java,xml}"
  - "**/templates/system/menu*.html"
---
# 메뉴 (/system/menu-manage)

- 계층형 메뉴 CRUD + 트리 조회. `TB_MENU`, `TB_MENU_AUTH`.
- 저장 시 세션의 사이드바 메뉴 실시간 갱신.
- 부모-자식 무결성(순환 참조 금지) 검증.
