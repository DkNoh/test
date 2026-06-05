---
paths:
  - "**/templates/**/*.html"
---
# Thymeleaf 규칙

- Layout Dialect 사용: `layout:decorate="~{defaultLayout}"`, 본문은 `layout:fragment="content"`.
- 정적 리소스는 `th:src="@{/js/...}"`, `th:href="@{/css/...}"` 처럼 `@{}` 절대경로. (CDN 금지 = 폐쇄망)
- 화면별 JS는 `static/js/<도메인>/<화면>-manage.js`로 분리하고 `<th:block layout:fragment="script">`에서 로드.
- TUI Grid 화면은 `<div id="grid">` + `<div id="pagination">` 패턴. 서버 페이징은 `/도메인/data`(ApiResponse<PageResponseDTO>) 호출.
- `th:` 오타·미선언 네임스페이스 주의 (렌더링 에러의 흔한 원인).
