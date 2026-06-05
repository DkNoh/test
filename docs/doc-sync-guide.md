# 문서 동기화 가이드

> 신규 화면을 추가할 때 코드와 문서가 어긋나지 않도록 유지하는 기준입니다.

## 대상 문서

| 문서 | 역할 |
|---|---|
| `docs/api-mapping.md` | 메뉴, URL, Controller, Template, 데이터 API 구현 상태 |
| `docs/menu-structure.md` | 조회조건과 그리드 출력 필드 |
| `docs/authority-model.md` | 역할/권한 기준 |
| `필수변경작업.md` | 운영/폐쇄망 이관 전 남은 필수 작업 |

## 스캐폴드 사용 시

- `/system/scaffold` 생성 결과에는 문서 갱신용 Markdown snippet이 함께 포함되어야 한다.
- 생성된 `docs-api-mapping.md snippet`은 `docs/api-mapping.md`의 해당 메뉴 표에 추가한다.
- 생성된 `docs-menu-structure.md snippet`은 `docs/menu-structure.md`의 해당 대메뉴 아래에 추가한다.
- 실제 DB 확인 전에는 상태를 `추정` 또는 `미확정`으로 표시한다.

## 최소 갱신 항목

- 메뉴명
- URL
- Controller
- Template
- JavaScript
- 데이터 API
- 조회조건
- 그리드 필드
- 구현 상태

## 권장 흐름

1. 개인 PC의 `local` 프로파일에서 스캐폴드로 화면 골격 생성
2. 생성 파일 배치
3. 메뉴 seed 또는 `메뉴등록.sql` 반영
4. 문서 snippet 반영
5. 실제 DB 확인 후 Mapper XML alias 보정
6. 검증 시나리오와 테스트 추가
