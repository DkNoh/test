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

## 테스트 (완료 조건 — 필수, 항상 적용)
- **모든 새 기능·수정은 그 동작을 검증하는 테스트를 함께 포함한다. 테스트 없는 변경은 "부분 완료"다.**
- 작업 시작 전, 해당 작업의 **검증 시나리오(입력 → 기대값)**를 먼저 확정한다. 스펙(`works/*.md`)이나 도메인 룰에 시나리오가 없으면 만들고 시작한다.
- "완료"는 그 검증 시나리오가 테스트로 옮겨져 `mvn test` PASS + 빌드 성공일 때만 쓴다. **테스트가 0개여서 통과한 상태는 완료가 아니다.**
- 최소 커버리지 기준:
  - Service: 분기/예외 로직 (Mockito로 Mapper 목킹, given/when/then).
  - Mapper: 페이징 경계(첫·끝·끝+1) + 검색조건 적용. 특히 `count`와 `selectList`의 `totalCount` 일치.
  - Controller: `@Valid` 검증 실패 시 에러 응답 포맷(`ApiResponse`).
- 테스트 작성 *방법*(Mockito / `@MybatisTest` 등)은 `.claude/rules/testing.md` 참조 — 테스트 파일을 열면 자동 로드된다. 이 섹션은 *방법*이 아니라 *강제*다.

## 보안 / 개인정보
- 개인정보(전화번호·이름 등) 조회·수정 메서드엔 `@PrivacyLog` 부착 (AOP가 자동 감사 기록).
- 화면 표시 시 `MaskingUtil`로 마스킹. 현재 사용자 정보는 `SecurityUtil`로 취득.
- 인가는 Spring Security(URL 1차) + `MenuAuthInterceptor`(`TB_MENU_AUTH` 기반 동적 2차).

## 프로파일
- dev: Oracle 직접 JDBC + In-Memory(admin/admin1234), Thymeleaf 캐시 off, 로그 DEBUG.
- prod: JNDI DataSource + LDAP, Thymeleaf 캐시 on, 로그 INFO/WARN.

## 문서 동기화 의무 (Documentation)
- 신규 화면(Controller/Template/JS) 추가 또는 조회 조건/그리드 필드가 변경된 경우, 개발 완료 시점에 다음 두 문서를 반드시 갱신한다.
  1. `docs/api-mapping.md`: 신규 기능의 [소메뉴, 상태, URL, Controller, Template] 항목을 표에 추가.
  2. `docs/menu-structure.md`: 신규/수정된 화면의 [조회조건(Search Inputs), 데이터 필드(Grid Columns)]를 기존 양식 표에 추가.
- 위 두 파일의 갱신 내용이 PR/작업 내역에 포함되지 않으면 해당 작업은 "완료"가 아닌 "부분 완료"로 간주한다.

## 레거시 마이그레이션 상태 구분
- `docs/menu-structure.md`의 조회조건과 그리드 필드는 폐쇄망 레거시 화면을 눈으로 관찰해 작성한 화면 명세다.
- 현재 DB 스키마, VO/DTO 필드명, Mapper SQL은 실제 운영 DB와 1:1 보장되지 않는다.
- AI가 생성한 필드명은 임시 naming으로 간주한다.
- 실제 DB 컬럼명/레거시 쿼리/업무 규칙이 확인되면 문서, schema, VO/DTO, Mapper XML, Service, JS를 함께 동기화한다.
- 실제 DB 근거 없이 필드명을 “확정”이라고 표현하지 않는다.
- 불확실한 필드는 TODO 또는 추정 표시를 남기고, 임의로 하드코딩하지 않는다.