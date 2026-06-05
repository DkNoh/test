---
paths:
  - "**/mapper/**/*.xml"
  - "**/resources/mapper/**/*.xml"
---
# MyBatis + Oracle 19c 규칙

## 페이징 — OFFSET/FETCH (ROWNUM 금지)
Oracle 19c이므로 ROWNUM 3중 중첩 대신 OFFSET/FETCH를 쓴다.

```sql
SELECT A.*
FROM ( /* 원본 조회 쿼리 */ ) A
<include refid="searchConditions"/>
ORDER BY A.정렬컬럼 DESC, A.PK DESC          -- 결정적 정렬 필수 (tie-breaker로 PK)
OFFSET #{offset} ROWS FETCH NEXT #{size} ROWS ONLY
```

- `offset = (page - 1) * size` (PageRequestDTO에서 계산).
- ORDER BY가 없으면 페이지 간 행 순서가 보장되지 않는다 → 반드시 결정적 정렬.
- 전체 건수는 `count` 쿼리로 분리하고, **count와 selectList에서 `searchConditions`(<where>) 적용 위치를 동일하게** 둔다 (안 그러면 totalCount != 목록 건수).

## 결과 매핑
- `resultType`은 `com.example.sms.vo.<도메인>.<Domain>VO` (VO는 `vo/` 하위. `dto/` 아님).
- 컬럼은 Oracle 대문자(SNAKE) ↔ Java camelCase 자동 매핑: `mybatis.configuration.map-underscore-to-camel-case=true`에 의존.
- LIKE 검색은 `'%' || #{kw} || '%'`.

## PK 채번
- 시퀀스 + `<selectKey keyProperty="..." order="BEFORE">SELECT seq.NEXTVAL FROM DUAL</selectKey>`.

## 동적 쿼리
- 검색 조건은 `<sql id="searchConditions"><where>...<if>...</if></where></sql>`로 공통화한다.
