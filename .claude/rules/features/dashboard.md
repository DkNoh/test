---
paths:
  - "**/*Dashboard*.{java,xml}"
  - "**/templates/index*.html"
  - "**/static/js/dashboard*.js"
---
# 대시보드 (/)

- 비동기 REST API로 통계 제공: `DashboardApiController` (`@RestController`).
- 화면 진입은 `/`, 데이터는 별도 API. `ApiResponse` 포맷 유지.
- 조회 전용 집계 → `@Transactional(readOnly = true)`.
