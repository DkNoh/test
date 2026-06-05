# BASE 프로젝트 적용 체크리스트

> 이 문서는 `sms-base`를 다른 폐쇄망/내부 업무시스템의 출발점으로 가져갈 때 먼저 확인할 항목입니다.

## 1. 프로젝트 식별자 변경

- `pom.xml`
  - `groupId`
  - `artifactId`
  - `version`
- Java package
  - 기본 패키지: `com.example.sms`
  - 새 프로젝트에 맞게 일괄 변경할 경우 Mapper XML namespace, `type-aliases-package`, 테스트 패키지도 함께 변경한다.
- 화면 표시명
  - `defaultLayout.html`, 로그인 화면, 대시보드, 메뉴 seed의 시스템명을 프로젝트명에 맞게 수정한다.

## 2. 환경 설정

- `src/main/resources/application.yml`은 ENV 기반 설정을 우선 사용한다.
- 로컬 개발자는 `.env.example` 또는 `application-local.example.yml`을 참고해 개인 환경을 맞춘다.
- 운영/폐쇄망 이관 시 DB 접속정보와 LDAP 정보는 코드에 직접 두지 않는다.
- 프로파일 용도:
  - `local`: 개인 PC 전용. 스캐폴드 생성기 활성화.
  - `dev`: 개발서버. 스캐폴드 생성기 비활성화.
  - `prod`: 운영서버. 스캐폴드 생성기 비활성화.

## 3. 인증/인가

- dev: 인메모리 계정은 로컬 개발용으로만 사용한다.
- prod: `SecurityConfig`의 임시 인증을 제거하고 LDAP/AD 인증으로 교체한다.
- 메뉴 노출은 `TB_MENU`, `TB_MENU_AUTH` 기준으로 처리한다.
- 실제 URL 접근은 `MenuAuthInterceptor` 또는 Spring Security에서 서버 측으로 차단한다.

### LDAP/AD 전환 방법

현재 BASE의 권장 구조는 다음과 같다.

- LDAP/AD: 아이디/비밀번호 인증만 담당
- DB 사용자 테이블: 사용자명, 사용여부, 역할, 메뉴 권한 담당
- 로그인 성공 후: `EmployeeMapper.findById(username)`로 DB 사용자 정보를 조회해 `userRole`, `userMenus`를 세션에 적재

운영 LDAP로 전환할 때는 `SecurityConfig`의 `prod` 임시 인메모리 인증을 제거하고 LDAP 인증 Provider로 교체한다.
이때 기존 로그인 성공 핸들러의 DB 권한 조회 흐름은 유지한다.

기본 변경 대상 ENV:

```text
LDAP_URL=ldap://ldap.company.local:389
LDAP_BASE_DN=dc=company,dc=local
LDAP_USER_SEARCH_BASE=
LDAP_USER_SEARCH_FILTER=(sAMAccountName={0})
```

AD 환경에서는 보통 `LDAP_USER_SEARCH_FILTER=(sAMAccountName={0})` 또는 `(userPrincipalName={0})`를 사용한다.
일반 LDAP 환경에서는 `(uid={0})`를 사용할 수 있다.

사용자 DN이 항상 같은 패턴이면 다음 방식도 가능하다.

```text
LDAP_USER_DN_PATTERN=uid={0},ou=users
```

단, 금융권/사내 AD는 사용자 OU가 부서별로 나뉘는 경우가 많으므로 `user-search-filter` 방식을 기본으로 권장한다.
즉 URL과 base-dn만으로 모든 환경을 보장하기보다는, 검색 필터까지 ENV로 열어두는 것이 BASE 프로젝트에 더 적합하다.

전환 후 확인할 항목:

- LDAP 인증 성공/실패
- LDAP 성공 후 DB 사용자 미등록 시 처리 정책
- DB 사용자 `USE_YN` 또는 잠금 상태 체크
- DB 사용자 역할(`ROLE_ADMIN`, `ROLE_USER` 등) 세션 적재
- 권한별 좌측 메뉴 조회
- 미인가 URL 직접 접근 차단
- 로그인 성공/실패 감사 로그 필요 여부

## 4. 메뉴/화면 추가 절차

1. 개인 PC에서 `SPRING_PROFILES_ACTIVE=local`로 실행한 뒤 `/system/scaffold`에서 SELECT 기반 화면 골격을 생성한다.
2. 생성된 Java/Mapper XML/HTML/JS 파일을 실제 패키지 위치에 배치한다.
3. 생성 결과의 `메뉴등록.sql`을 dev DB 또는 seed 코드에 반영한다.
4. 생성 결과의 `docs-api-mapping.md snippet`, `docs-menu-structure.md snippet`을 문서에 반영한다.
5. 실제 DB 컬럼이 확인되면 MyBatis XML에서 `AS javaFieldName` alias로 Java 변경을 최소화한다.

## 5. 운영 전 필수 확인

- `필수변경작업.md`의 항목을 모두 확인한다.
- `mvn test`가 더미 데이터 삽입 없이 검증 테스트만 수행하는지 확인한다.
- `mvn -DskipTests package`로 WAR 빌드가 성공하는지 확인한다.
- dev/prod 프로파일에서 스캐폴드와 임시 계정이 노출되지 않는지 확인한다.
