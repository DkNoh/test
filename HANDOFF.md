# sms-project-v2 — Claude Code 인계 문서

이 저장소에는 Claude Code용 규칙이 설정돼 있다. 이 문서는 (1) 적용된 결정과 (2) 아직 고쳐야 할 문제 목록이다.

CLI 첫 세션에서 이렇게 시작하면 된다:
> "HANDOFF.md 읽고, '고쳐야 할 문제'의 A 항목부터 같이 진행하자."

## 설정된 규칙 파일
- `CLAUDE.md` (루트): 절대금지·구현품질·완료판정·실서버 안전 — 항상 로드.
- `.claude/rules/project.md`, `vcs.md`: 항상 로드되는 공통 컨벤션.
- `.claude/rules/{mybatis-oracle,controller,thymeleaf,testing,scaffold}.md`: 해당 파일을 열 때만 로드(path-scoped).
- `.claude/rules/features/*.md`: 해당 도메인 파일을 열 때만 로드.
- 무엇이 로드됐는지는 CLI에서 `/memory` 로 확인.

## 핵심 결정
1. Oracle 19c → 페이징은 OFFSET/FETCH (ROWNUM 금지). ORDER BY 필수.
2. VO는 `com.example.sms.vo.<도메인>` (dto 아님). 2026-06-02 리팩토링 반영.
3. 새 CRUD 화면은 `/system/scaffold`(로컬 전용 생성기)로 생성 후 보정.

## 현재 작업 목표 (체크리스트)

### A. 신규 화면 개발
- [ ] 스캐폴드를 이용한 신규 화면 생성 및 비즈니스 로직 작성 대기 중

## 스캐폴드 후속 지시 프롬프트 (복사/붙여넣기용)
> 스캐폴드로 8개 파일 생성 직후, 새 세션에서 아래 텍스트를 복사하여 AI에게 지시하세요.

```text
방금 스캐폴드를 이용해 [ 예: 캠페인 관리 ] 신규 화면용 8개 파일을 생성했어.
해당 파일들을 읽고 다음 3가지만 최우선으로 진행해 줘.

1. **바인딩 및 로직 점검**: HTML의 Grid 컬럼과 검색 조건이 Controller/XML 파라미터와 제대로 매핑되었는지 확인하고, 추가로 구현해야 할 비즈니스 로직(저장, 삭제 분기 처리 등)을 파악해서 알려줘.
2. **문서 동기화**: docs/api-mapping.md와 docs/menu-structure.md에 방금 추가된 화면의 URI 매핑과 화면 컬럼 구조를 업데이트해 줘.
3. **핸드오프 스냅샷**: 파악된 '세부 비즈니스 로직 작업'을 HANDOFF.md에 '- [ ]' 체크리스트로 요약해 줘.
```

## 참고
- 각 룰의 path-scoped glob은 실제 파일명에 맞게 조정 가능 (`.claude/rules/*.md` 상단 `paths`).
## 2026-06-07 Local Oracle SQL Export Handoff
- Local Docker Oracle 접속 확인 완료: `SYSTEM/1234@//localhost:1521/SMS`.
- 프로젝트 관련 export 대상 테이블은 총 24개:
  `TB_APPROVAL`, `TB_APPROVAL_HIST`, `TB_ATTACH_FILE`, `TB_AUTHORITY`, `TB_BANK`,
  `TB_CONTACT`, `TB_CONTACT_GROUP`, `TB_CURRENCY`, `TB_CUSTOMER_SSN`, `TB_DEP`,
  `TB_DEPT`, `TB_EMP`, `TB_EXCHANGE_RATE`, `TB_GROUP_CONTACT_MAP`, `TB_MENU`,
  `TB_MENU_AUTH`, `TB_MESSAGE`, `TB_PRIVACY_AUDIT_LOG`, `TB_RATE_PROVIDER`,
  `TB_SMS_HISTORY`, `CAMPAIGN`, `EMPLOYEE`, `SMS_HISTORY`, `SMS_SEND_LOG`.
- SQL export 생성 스크립트: `tools/export_project_tables_sql.sql`.
- 생성된 로컬 더미 데이터 SQL 위치: `db/export/sms-project-v2-project-tables.sql`.
  두 로컬 개발 환경 간 이동 목적의 더미 데이터이므로 git commit 대상에 포함해도 된다.
- export 규칙: `SMS_HISTORY`는 10,010건 중 500건만 포함, 나머지 대상 테이블은 전체 데이터 포함.
- 최종 파일 검증 결과: 전체 INSERT 1,667건, `SMS_HISTORY` INSERT 500건, `GENERATED ALWAYS AS IDENTITY` 없음.
- 다른 PC에서 재생성하려면 Oracle 컨테이너가 떠 있는 상태에서 아래를 실행:
  `Get-Content -Raw .\tools\export_project_tables_sql.sql | docker exec -i oracle sqlplus -L -S SYSTEM/1234@//localhost:1521/SMS`
  이후 컨테이너 `/tmp/sms-project-v2-project-tables.sql`을 필요한 로컬 위치로 복사한다.
- 생성된 SQL은 맨 앞에 `drop table ... cascade constraints purge;`가 있으므로 기존 schema에 실행하면 동일 테이블을 삭제 후 재생성한다.
