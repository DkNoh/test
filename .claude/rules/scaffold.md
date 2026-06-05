---
paths:
  - "**/SystemScaffoldController.java"
---
# 스캐폴드 생성기 불변식 (로컬 전용 코드 공장)

`/system/scaffold`는 쿼리를 넣으면 CRUD 코드를 찍어내는 로컬 전용 생성기다.
**생성기는 로컬 전용이지만 생성물은 배포되므로**, 템플릿은 아래 불변식을 항상 만족해야 한다.
하나라도 깨지면 생성물 전체가 같은 결함을 양산하므로, 생성기 변경 시 최우선 점검 대상이다.

## 생성물 불변식 (항상 유지)
1. 페이징 템플릿은 OFFSET/FETCH를 쓴다 (ROWNUM 3중 중첩 금지). `mybatis-oracle.md` 참조.
2. VO 생성 패키지는 `com.example.sms.vo.<module>`. Mapper import·XML `resultType`도 `vo.`로 일치시킨다.
3. 엑셀 경로는 하나로 통일한다: 클래스명 `ExcelUtil`(단수), 반환 타입을 `/generate`와 `/generate-excel-snippet`에서 동일하게.
4. `count`와 `selectList`의 `searchConditions`(`<where>`) 적용 위치를 동일하게 둔다 (`totalCount ≠ 목록 건수` 방지).
5. 저장 컨트롤러에 `@Valid`, 생성되는 VO/DTO에 검증 애너테이션을 포함한다.
6. JS의 apiUrl 접근은 `pageBuilder.config.apiUrl`로 통일한다.
7. 생성 컨트롤러에 `@Profile("dev")`를 붙인다 (WAR 배포 시 prod 노출 차단).

## 권장 / 주의
- 미사용 `createFile`/`createJavaFile`는 두지 않는다. 다시 연결한다면 입력 경로 검증(path traversal 방지)을 먼저 넣는다.
- 쿼리 파싱이 콤마 split 기반이라 함수 내 콤마(`NVL`/`DECODE`/`CASE`/`TO_CHAR`)에 취약하다 → 단순 쿼리만 자동 생성, 복합 쿼리는 수동 보정.

## 날짜 입력 필드 기본값 규칙
- 생성된 HTML에 `type="date"` 또는 `type="datetime-local"` 필드가 있으면 **당일 날짜를 기본값으로 셋팅**한다 (시작일·종료일 계열 모두 당일).
- 날짜 필드 감지: 변수명에 `date`, `dt`, `at` 포함 시 `date` 타입으로 생성.
- 셋팅 코드는 JS `DOMContentLoaded` 안에 두고, 이미 값이 있으면(`!el.value`) 덮어쓰지 않는다.

```javascript
// 날짜 필드 당일 기본값 셋팅 (date / datetime-local 공통)
const today = new Date().toISOString().split('T')[0]; // 'YYYY-MM-DD'
document.querySelectorAll('input[type="date"]').forEach(el => {
    if (!el.value) el.value = today;
});
document.querySelectorAll('input[type="datetime-local"]').forEach(el => {
    if (!el.value) el.value = today + 'T00:00';
});
```

## 생성기 변경 시 검증
- 생성기 템플릿을 수정하면, 샘플 쿼리 1개로 생성을 돌려 **생성물이 위 불변식 1~7을 만족하는지** 확인한다.
- 가능하면 이 점검을 테스트로 고정한다 (생성기는 "공장"이므로 한 번의 결함이 전 화면에 퍼진다).
