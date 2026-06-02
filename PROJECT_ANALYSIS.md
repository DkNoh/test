# SMS 발송관리 시스템 (sms-project-v2) 분석 문서

> 작성일: 2026-06-02

---

## 1. 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | SMS 발송관리 시스템 (sms-project-v2) |
| 목적 | 금융권(은행) 내부 SMS/LMS/알림톡 대량 발송 및 이력 관리 |
| 패키징 | WAR (외장 WAS JBoss EAP 배포 대응) |
| 개발 환경 | Spring Boot 3.3.0 / Java 21 / Oracle DB |
| 빌드 도구 | Maven |

---

## 2. 기술 스택

| 분류 | 기술 |
|------|------|
| 백엔드 프레임워크 | Spring Boot 3.3.0 (Web, Security, AOP, Validation) |
| 언어 | Java 21 |
| DB | Oracle (ojdbc11), 로컬 개발은 Oracle 11g/XE 호환 |
| ORM/SQL | MyBatis 3.0.3 (XML Mapper 방식) |
| 뷰 엔진 | Thymeleaf + Layout Dialect |
| 보안 | Spring Security 6 (Form 로그인, BCrypt 암호화) |
| LDAP | Spring LDAP (운영 환경 연동 예정) |
| SQL 로깅 | log4jdbc-log4j2 |
| AOP | Spring AOP (개인정보 감사 로그) |
| 엑셀 | Apache POI 5.2.3 |
| 유틸 | Lombok |
| 테스트 | Spring Boot Test, MyBatis Test |

---

## 3. 프로젝트 디렉토리 구조

```
sms-project-v2/
├── pom.xml                          # Maven 빌드 설정
├── oracle_schema.sql                # Oracle DDL (DEP, EMP, sms_history)
├── schema.sql                       # PostgreSQL DDL (개발 참고용)
├── audit_table.sql                  # 감사 로그 테이블 DDL
├── insert_10000.sql                 # 대량 더미 데이터
├── src/
│   └── main/
│       ├── java/com/example/sms/
│       │   ├── SmsApplication.java          # 스프링 부트 진입점
│       │   ├── annotation/
│       │   │   └── PrivacyLog.java           # 개인정보 감사 커스텀 어노테이션
│       │   ├── aop/
│       │   │   └── PrivacyLogAspect.java     # AOP 기반 감사 로그 처리
│       │   ├── config/
│       │   │   ├── SecurityConfig.java       # Spring Security 설정
│       │   │   ├── WebMvcConfig.java         # MVC / 인터셉터 등록
│       │   │   ├── MenuAuthInterceptor.java  # URL 기반 동적 접근 제어
│       │   │   ├── TableInitRunner.java      # 앱 기동 시 DB 자동 초기화
│       │   │   └── ValidationCheck.java     # 유효성 검사 유틸
│       │   ├── controller/
│       │   │   ├── account/                  # 로그인 / 사원 관리
│       │   │   ├── api/                      # 대시보드 REST API
│       │   │   ├── approval/                 # 결재 관리
│       │   │   ├── common/                   # 공통(파일, 인덱스, 기본화면)
│       │   │   ├── contact/                  # 주소록 / 그룹
│       │   │   ├── sms/                      # 발송이력 / 캠페인 / 통계
│       │   │   └── system/                   # 부서 / 메뉴 / 공통코드 / 권한
│       │   ├── dto/                          # 요청/응답 DTO
│       │   ├── exception/                    # 전역 예외 처리
│       │   ├── mapper/                       # MyBatis 인터페이스
│       │   ├── service/                      # 비즈니스 로직
│       │   ├── util/                         # Excel, Masking, Security 유틸
│       │   └── vo/                           # DB 조회 결과 VO (도메인별 분리)
│       │       ├── common/                   # 공통 VO (AttachFileVO 등)
│       │       ├── contact/                  # 주소록 도메인 VO
│       │       ├── sms/                      # SMS 도메인 VO
│       │       └── system/                   # 시스템/부서/직원 도메인 VO
│       └── resources/
│           ├── application.yml              # 설정 (dev/prod 프로파일)
│           ├── logback-spring.xml           # 로그 설정
│           ├── mapper/**/*.xml              # MyBatis SQL XML
│           ├── templates/**/*.html          # Thymeleaf 화면
│           └── static/                      # CSS / JS / 이미지
```

---

## 4. 주요 기능 모듈

### 4-1. SMS 발송이력 관리 (`/sms/history`)
- 발송 유형: `SMS`, `LMS`, `ALIMTALK`
- 발송 상태: `WAIT`, `SUCCESS`, `FAIL`
- 재발송 여부 관리 (`resend_yn`)
- 조회 / 저장 / 삭제 / 페이징 지원

### 4-2. 캠페인 관리 (`/campaign`, `/sms/campaign`)
- 발송 대상 관리 (`/campaign/target-manage`)
- 캠페인 검색 (`campaignSearch`)
- TB_CAMPAIGN 테이블 기반

### 4-3. 결재 관리 (`/approval`)
- 발송 전 결재 요청/승인/반려 처리
- 일괄 처리(배치 승인) 지원
- 결재 상태: `PENDING`, `APPROVED`, `REJECTED`

### 4-4. 주소록 관리 (`/contact`)
- 개별 연락처 CRUD (`TB_CONTACT`)
- 그룹 관리 (`TB_CONTACT_GROUP`)
- 그룹-연락처 매핑 (`TB_GROUP_CONTACT_MAP`)
- 엑셀 다운로드 지원

### 4-5. 권한 관리 (`/authority/manage`)
- 시스템 권한 코드 등록/삭제
- 메뉴별 접근 권한 매핑

### 4-6. 메뉴 관리 (`/system/menu-manage`)
- 동적 메뉴 CRUD 및 트리 구조 조회
- 메뉴 저장 시 세션의 사이드바 메뉴 실시간 갱신
- 계층형 메뉴 (`TB_MENU`, `TB_MENU_AUTH`)

### 4-7. 부서/사원 관리 (`/system/dept`)
- 부서 (`SMS.DEP`) 및 사원 (`SMS.EMP`) CRUD

### 4-8. 대시보드 (`/`)
- 비동기 REST API로 통계 데이터 제공 (`DashboardApiController`)

### 4-9. 통계 (`/sms/statistics`, `/sms/dept-stat`)
- 마케팅 수신거부 통계
- 부서별 발송 통계

### 4-10. 파일 첨부 (`/common/file`)
- 파일 업로드/다운로드 (로컬 디스크: `C:/Work/upload`)
- `attach_file` 테이블 관리

---

## 5. DB 테이블 구조

| 테이블명 | 설명 |
|----------|------|
| `SMS.EMP` | 사원 (로그인 계정, 권한 플래그 다수 포함) |
| `SMS.DEP` | 부서 |
| `sms_history` | SMS 발송 이력 |
| `TB_SMS_HISTORY` | 발송 이력 (별도 형태, 히스토리 조회용) |
| `TB_CONTACT` | 주소록 연락처 |
| `TB_CONTACT_GROUP` | 연락처 그룹 |
| `TB_GROUP_CONTACT_MAP` | 그룹-연락처 매핑 |
| `TB_CAMPAIGN` | 캠페인 |
| `TB_MENU` | 메뉴 |
| `TB_MENU_AUTH` | 메뉴-권한 매핑 |
| `privacy_audit_log` | 개인정보 감사 로그 |
| `attach_file` | 첨부파일 |

### EMP 주요 권한 컬럼
| 컬럼 | 의미 |
|------|------|
| `EMP_LEV` | 사용자 등급 (`9` = 관리자) |
| `PERM_SYS` | 시스템 관리 권한 |
| `PERM_CPN` | 캠페인 권한 |
| `PERM_STA` | 통계 권한 |
| `PERM_PSN` | 개인정보 권한 |
| `PERM_AUT` | 결재 권한 |
| `PERM_MMS` | MMS 권한 |

---

## 6. 보안 아키텍처

### 인증 (Authentication)
- **개발(dev)**: In-Memory 계정 (`admin / admin1234`)
- **운영(prod)**: 사내 LDAP(AD) 연동 예정 (현재는 임시 In-Memory)
- 로그인 성공 시 세션에 `loginIp`, `loginTime`, `userName`, `userRole`, `userMenus` 저장

### 인가 (Authorization)
- Spring Security: URL 기반 1차 인가
- `MenuAuthInterceptor`: DB에 등록된 `TB_MENU_AUTH` 기반 **동적 2차 인가**
  - `/data`, `/save`, `/delete` 등 API URI는 부모 화면 URL로 치환하여 검증
  - Referer 기반 Master-Detail API 허용 (부모 화면 권한이 있으면 하위 API 허용)

### 개인정보 감사 로그 (Privacy Audit)
- `@PrivacyLog` 어노테이션 + `PrivacyLogAspect` AOP
- 개인정보 조회/수정 시 **자동으로** 실행자 ID, IP, URL, 파라미터를 `privacy_audit_log` 테이블에 기록
- 파라미터 최대 500자 저장 (초과 시 truncate)

### CSRF
- 현재 비활성화 (폐쇄망 환경)
- 활성화 절차가 SecurityConfig.java 주석에 상세 기술됨

---

## 7. 아키텍처 패턴

### Two-Track 전략
각 Controller는 두 가지 역할을 단일 `@RequestMapping`에서 통합:
- **Track 1**: `GET /xxx` → Thymeleaf 화면 렌더링 (String 반환)
- **Track 2**: `GET /xxx/data`, `POST /xxx/save` 등 → JSON API (`ResponseEntity<ApiResponse<T>>`)

### 공통 응답 포맷
```json
{
  "success": true,
  "data": { ... },
  "message": "..."
}
```
- `ApiResponse<T>` 클래스로 통일

### 페이징
- `PageRequestDTO` (page, size, 검색 파라미터)
- `PageResponseDTO<T>` (data, totalCount, page, size)
- MyBatis XML에서 Oracle ROWNUM 기반 페이징 SQL

### 환경 분리 (Profile)
```
spring.profiles.active: dev  → Oracle 직접 접속 + In-Memory 계정
spring.profiles.active: prod → JBoss JNDI DataSource + LDAP 인증
```

---

## 8. 계층별 흐름

```
브라우저 (Thymeleaf)
    ↓ HTTP Request
[MenuAuthInterceptor]  ← TB_MENU_AUTH (DB) 실시간 동적 인가
    ↓ 통과
[Controller]           ← @RequestMapping, @GetMapping/@PostMapping
    ↓
[Service]              ← 비즈니스 로직, 트랜잭션
    ↓
[Mapper Interface]     ← MyBatis
    ↓
[XML Mapper]           ← SQL 쿼리 (src/main/resources/mapper/**/*.xml)
    ↓
[Oracle DB]
```

---

## 9. 주요 유틸리티

| 클래스 | 역할 |
|--------|------|
| `ExcelUtil` | Apache POI 기반 엑셀 생성/다운로드 |
| `MaskingUtil` | 전화번호, 이름 등 개인정보 마스킹 |
| `SecurityUtil` | Spring Security Context에서 현재 사용자 정보 추출 |

---

## 10. 로깅 설정

- **애플리케이션 로그**: `logs/sms-project-{날짜}.log` (일별 롤링, Logback)
- **SQL 로그**: log4jdbc를 통해 실제 실행 SQL 및 실행 시간 출력
- **개인정보 감사 로그**: `privacy_audit_log` 테이블 (DB 영구 보관)
- 로그 레벨: `com.example.sms` → DEBUG

---

## 11. 개발/운영 전환 체크리스트

| 항목 | 개발(dev) | 운영(prod) |
|------|-----------|-----------|
| DB 접속 | Oracle 직접 JDBC | JBoss JNDI (java:/comp/env/jdbc/SMS) |
| 인증 | In-Memory (admin/admin1234) | LDAP(AD) 연동 |
| CSRF | 비활성화 | 활성화 권장 |
| Thymeleaf 캐시 | false | true |
| 파일 업로드 경로 | C:/Work/upload | 운영 서버 경로로 변경 |
| 로그 레벨 | DEBUG | INFO 또는 WARN |

---

## 12. 향후 개선 포인트

1. **CSRF 활성화**: 보안 심사 대응을 위해 SecurityConfig.java 주석 가이드 참조
2. **LDAP 연동 완성**: `prodUserDetailsService`를 실제 LDAP 바인딩 코드로 교체
3. **개인정보 감사 로그 비동기화**: `auditLogService.saveLog()` 를 `@Async`로 변경 시 응답속도 향상
4. **파일 업로드 경로 외부화**: `app.upload.dir` 설정값을 환경변수로 분리
5. **`.bak` 파일 정리**: `.java.bak`, `.xml.bak` 파일 제거하여 코드베이스 정돈

---

## 13. 주요 리팩토링 이력

### 13-1. VO 패키지 도메인 분리 (2026-06-02)
- **배경**: `dto` 패키지 하위와 `vo` 루트에 무작위로 흩어져 있던 VO 객체들을 체계적으로 관리할 필요성 대두.
- **내용**: 도메인 주도 설계(DDD) 패턴에 맞게 `vo` 패키지 하위로 모든 VO 객체를 이동(`vo/sms`, `vo/system`, `vo/contact` 등).
- **영향도**: 약 50여 개의 `Controller`, `Service`, `Mapper(XML)` 파일 내 `import` 구문 및 `resultType/parameterType` 경로 일괄 수정 완료.
- **특이사항**: 네이밍 컨벤션에 어긋났던 `campaignSearchVO` 클래스명을 `CampaignSearchVO`로 교정함.
