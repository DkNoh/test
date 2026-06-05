# 프로젝트 공통 컨벤션

## 컨트롤러 (Two-Track)
하나의 `@Controller` + `@RequestMapping("/도메인")` 안에서:
- 화면: `@GetMapping` → Thymeleaf 뷰 이름(String) 반환
- 데이터: `@ResponseBody @GetMapping("/data")` → `ResponseEntity<ApiResponse<PageResponseDTO<VO>>>`
- 저장/삭제: `@ResponseBody @PostMapping("/save"|"/delete")` → `ResponseEntity<ApiResponse<T>>`

## 공통 응답
- `ApiResponse<T>` { success, data, message } 로 통일 (`com.example.sms.dto.common`).
- 목록은 `ApiResponse<PageResponseDTO<VO>>`.

## 페이징
- 요청: `PageRequestDTO`(page, size, 검색조건)를 상속한 `<Domain>SearchRequestDTO`.
- 응답: `PageResponseDTO.of(list, request, totalCount)`.

## DTO / VO 경계 (고정)
- 요청 파라미터 = `dto/<도메인>`의 `*SearchRequestDTO` / `*RequestDTO`.
- DB 조회 결과 = `vo/<도메인>`의 `*VO`.
- 2026-06-02 리팩토링: 모든 VO는 `vo/` 하위 도메인 패키지에 둔다 — `vo/sms`, `vo/system`, `vo/contact`, `vo/common`.
- 신규 코드에서 `Map` 반환 지양. 동적 컬럼 등 불가피한 경우만 예외.

## 트랜잭션 / 예외 / 검증 (계약)
- `@Transactional`은 Service 계층에만. 조회는 `@Transactional(readOnly = true)`.
- 예외는 `@RestControllerAdvice` 한 곳에서 `ApiResponse` 에러 포맷으로 변환. 컨트롤러에서 try-catch로 삼키지 않는다.
- 입력 검증은 `@Valid` + Bean Validation 우선. 복합 검증만 `ValidationCheck` 유틸.

## 보안 / 개인정보
- 개인정보(전화번호·이름 등) 조회·수정 메서드엔 `@PrivacyLog` 부착 (AOP가 자동 감사 기록).
- 화면 표시 시 `MaskingUtil`로 마스킹. 현재 사용자 정보는 `SecurityUtil`로 취득.
- 인가는 Spring Security(URL 1차) + `MenuAuthInterceptor`(`TB_MENU_AUTH` 기반 동적 2차).

## 프로파일
- dev: Oracle 직접 JDBC + In-Memory(admin/admin1234), Thymeleaf 캐시 off, 로그 DEBUG.
- prod: JNDI DataSource + LDAP, Thymeleaf 캐시 on, 로그 INFO/WARN.
