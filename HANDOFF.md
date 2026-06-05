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
