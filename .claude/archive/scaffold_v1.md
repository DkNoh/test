---
paths:
  - "**/SystemScaffoldController.java"
---
# 스캐폴드 생성기 불변식 (로컬 전용 코드 공장)

`/system/scaffold`는 쿼리를 넣으면 CRUD 코드를 찍어내는 로컬 전용 생성기다.
**생성기는 로컬 전용이지만 생성물은 배포되므로**, 템플릿은 아래 규칙을 반드시 따른다.

## 반드시 (현재 코드와 어긋나 있어 수정 대상)
1. 페이징 템플릿은 OFFSET/FETCH (ROWNUM 3중 중첩 금지). `mybatis-oracle.md` 참조.
2. VO 생성 패키지 = `com.example.sms.vo.<module>` (현재 `dto.<module>`). Mapper import·XML `resultType`도 `vo.`로 일치.
3. 엑셀 경로 1개로 통일: 클래스명(`ExcelUtil` 단수)과 반환 타입을 `/generate`와 `/generate-excel-snippet`에서 일치. (현재 `ExcelUtil`/`ExcelUtils`, `List<Map>`/`List<VO>` 혼재)
4. `count`와 `selectList`의 `searchConditions` 적용 위치를 동일하게.
5. 저장 컨트롤러에 `@Valid`, VO/DTO에 검증 애너테이션 포함.
6. JS의 apiUrl 접근 방식 통일 (`pageBuilder.config.apiUrl` vs `pageBuilder.apiUrl`).

## 권장
- 컨트롤러에 `@Profile("dev")` (WAR 배포 시 prod 노출 차단).
- `createFile`/`createJavaFile`는 현재 미사용(dead code) → 제거. 연결한다면 입력값 경로 검증(path traversal 방지).
- 쿼리 파싱이 콤마 split 기반이라 함수 내 콤마(NVL/DECODE/CASE/TO_CHAR)에 취약 → 단순 쿼리만, 복합은 수동 보정.

## 날짜 입력 필드 기본값 규칙
- 생성된 HTML에서 `type="date"` 또는 `type="datetime-local"` 입력 필드가 있으면 **당일 날짜를 기본값으로 셋팅**한다.
- JS 템플릿에서 날짜 필드 감지 기준: 변수명에 `date`, `dt`, `at` 포함 시 `date` 타입으로 생성 (기존 규칙 유지).
- 기본값 셋팅 코드는 JS의 `DOMContentLoaded` 안에 추가한다.

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

- 단, 종료일(`endDate`, `endDt`) 계열은 당일, 시작일(`startDate`, `startDt`) 계열도 당일로 동일 셋팅한다.
- 이미 값이 있는 경우(`!el.value` 조건)는 덮어쓰지 않는다.
