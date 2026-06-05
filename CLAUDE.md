# sms-project-v2 작업 규칙

SMS/LMS/알림톡 대량 발송·이력 관리 시스템 (금융권 내부, 폐쇄망).
- 스택: Spring Boot 3.3 / Java 21 / Oracle 19c / MyBatis(XML) / Thymeleaf + Layout Dialect / Maven / WAR(JBoss EAP)
- 패키지: `com.example.sms`
- 계층: `controller/<도메인>` → `service/<도메인>` → `mapper/<도메인>`(인터페이스) → `resources/mapper/**/*.xml` → Oracle

## 절대 금지
1. fallback 금지 — 실패하면 원인을 수정한다. 대체 경로를 만들지 않는다.
2. 우회/꼼수 금지 — try-catch로 에러 삼키기, 하드코딩 분기, 조건부 무시 금지.
3. 에러 숨김 금지 — 에러는 즉시 전파한다. 로그만 찍고 정상 흐름 진행 금지.
4. 지시하지 않은 패턴/라이브러리 임의 도입 금지 — 먼저 승인을 받는다.
- 같은 에러가 3회 반복되면 중단하고 (에러 원문 + 시도한 3가지 + 추정 원인)을 보고한다.

## 구현 품질
- 운영 중 바뀔 값(타임아웃·경로·한도)은 `application.yml`로 외부화한다. 매직넘버/매직스트링은 상수로.
- 같은 로직이 2곳 이상이면 메서드로 추출한다(DRY). 함수 하나는 한 가지 일만.
- 파일 300줄 초과 시 분리를 검토한다.
- "동작하니까 끝"이라고 하지 않는다. 실행·검증까지 한다.

## 완료 판정
- "완료"는 `mvn test` PASS + `mvn -DskipTests package` 빌드 성공일 때만 쓴다.
- 미검증·게이트 실패 상태는 "부분 완료". 실행해 보지 않고 완료 보고 금지.

## 실서버 / DB 안전 (필수)
- 실서버·운영(prod) DB 작업은 반드시 사전 승인. 개발(dev) 환경 우선.
- 작업 전 `application.yml`의 active profile부터 확인 (dev=직접 JDBC / prod=JNDI `java:/comp/env/jdbc/SMS`).
- DB 접속정보·비밀번호는 코드나 이 파일에 두지 않는다. 개인 메모는 `CLAUDE.local.md`(gitignore).

## 상세 규칙
`.claude/rules/` 안에 항목별 규칙이 있다(일부는 해당 파일을 열 때만 자동 로드된다).
아직 고쳐야 할 문제는 `HANDOFF.md` 참조.
