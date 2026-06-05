# SMS 시스템 메뉴-개발 맵핑리스트

> 기준일: 2026-06-02  
> MENU.md(운영 화면 목록) ↔ 현재 코드베이스 매핑 현황

---

## 상태 범례

| 기호 | 의미 |
|------|------|
| ✅ | Controller + Template + 데이터 API 모두 존재 |
| ⚠️ | Template만 있고 Controller 미연결, 또는 데이터 API 미완성 |
| ❌ | 미개발 (파일 없음) |

---

## 메뉴 트리 & 매핑 현황

### 1. 기본메뉴

| # | 소메뉴 | 상태 | URL (현재/제안) | Controller | Template |
|---|--------|------|----------------|------------|----------|
| 1-1 | SMS관리 시스템 안내 | ❌ | `/basic/intro` | — | — |
| 1-2 | 공지사항 | ✅ | `/basic/notice` | `BasicController` | `basic/notice.html` |
| 1-3 | 메세지조회 | ❌ | `/basic/message` | — | — |
| 1-4 | 사용자조회 | ❌ | `/basic/user-search` | — | — |
| 1-5 | MFA사용자 관리 | ❌ | `/basic/mfa` | — | — |

---

### 2. SMS발송조회

| # | 소메뉴 | 상태 | URL (현재/제안) | Controller | Template |
|---|--------|------|----------------|------------|----------|
| 2-1 | 발송내역조회 | ✅ | `/sms/history` | `SmsHistoryController` | `sms/history-manage.html` |
| 2-2 | 고객별 조회 | ⚠️ | `/sms/customer-search` | **Controller 미연결** | `sms/customer-search.html` |
| 2-3 | 주민번호 조회 | ⚠️ | `/sms/ssn-search` | **Controller 미연결** | `sms/ssn-search.html` |

> **주의**: 2-2, 2-3은 Template 파일만 존재. Controller·Mapper·Service 개발 필요.

---

### 3. 캠페인 SMS

| # | 소메뉴 | 상태 | URL (현재/제안) | Controller | Template |
|---|--------|------|----------------|------------|----------|
| 3-1 | 발송대상관리 | ⚠️ | `/campaign/target-manage` | `CampaignController` | `campaign/target-manage.html` |
| 3-2 | 발송대상승인 | ✅ | `/approval` | `ApprovalController` | `approval/approval-manage.html` |
| 3-3 | SMS 등록 | ❌ | `/campaign/sms/register` | — | — |
| 3-4 | LMS 등록 | ❌ | `/campaign/lms/register` | — | — |
| 3-5 | 알림톡 등록 | ❌ | `/campaign/alimtalk/register` | — | — |
| 3-6 | SMS 승인 | ❌ | `/campaign/sms/approve` | — | — |
| 3-7 | LMS 승인 | ❌ | `/campaign/lms/approve` | — | — |
| 3-8 | 알림톡 승인 | ❌ | `/campaign/alimtalk/approve` | — | — |
| 3-9 | 발송내역조회 (SMS) | ⚠️ | `/sms/campaign` | `campaignSearchController` | `sms/campaign-manage.html` |
| 3-10 | LMS 발송내역조회 | ❌ | `/sms/campaign-lms` | — | — |
| 3-11 | 알림톡 발송내역조회 | ❌ | `/sms/campaign-alimtalk` | — | — |

> **주의**: 3-1(발송대상관리)은 화면은 있으나 데이터 API 연동 확인 필요.  
> 3-9는 현재 `CAMPAIGN` 테이블 기반이나 실제 운영 데이터 연결 미확인.

---

### 4. 시스템관리

| # | 소메뉴 | 상태 | URL (현재/제안) | Controller | Template |
|---|--------|------|----------------|------------|----------|
| 4-1 | 부서 관리 | ✅ | `/system/dept-manage` | `SystemDeptController` | `system/dept-manage.html` |
| 4-2 | 메세지 관리 | ❌ | `/system/message` | — | — |
| 4-3 | 카카오템플릿관리 | ❌ | `/system/kakao-template` | — | — |
| 4-4 | 광고성 메세지관리 | ❌ | `/system/ad-message` | — | — |
| 4-5 | 시간대별조회 | ⚠️ | `/sms/dept-stat` | `DeptStatController` | `sms/dept-stat-manage.html` |

> **주의**: 4-5는 현재 구현이 "부서별 통계"이고, 운영 메뉴의 "시간대별 조회" (시간대·전송중·실패·성공·총건수)와 **필드 구조가 다를 수 있음** — 실제 요구사항 재확인 필요.

---

### 5. 시스템관리 (계정관리)

| # | 소메뉴 | 상태 | URL (현재/제안) | Controller | Template |
|---|--------|------|----------------|------------|----------|
| 5-1 | 사용자관리 | ✅ | `/account/user-manage` | `AccountController` + `UserApiController` | `account/user-manage.html` |

---

### 6. 통계 관리

| # | 소메뉴 | 상태 | URL (현재/제안) | Controller | Template |
|---|--------|------|----------------|------------|----------|
| 6-1 | 마케팅 철회 통계 | ✅ | `/statistics/marketing-optout` | `StatisticsController` | `statistics/marketing-optout.html` |

---

## 개발됐지만 운영 MENU.md에 없는 화면 (내부 관리용)

| 화면명 | URL | Controller | Template | 비고 |
|--------|-----|------------|----------|------|
| 주소록(연락처) | `/contact/item` | `ContactController` | _(없음)_ | 데이터 API만 존재 |
| 주소록 그룹 | `/contact/group` | `ContactGroupController` | `contact/group-manage.html` | 완료 |
| 권한 관리 | `/authority/manage` | `AuthorityController` | `authority/authority-manage.html` | 완료 |
| 메뉴 관리 | `/system/menu-manage` | `MenuManageController` | `system/menu-manage.html` | 완료 |
| 대시보드 | `/` | `IndexController` | `index.html` | 완료 |
| 스캐폴드 생성기 | `/system/scaffold` | `SystemScaffoldController` | `system/scaffold.html` | dev 전용 |

---

## 개발 우선순위 제안

### 1순위 — Controller 연결만 하면 되는 것 (Template 존재)
- `2-2` 고객별 조회 → `/sms/customer-search` Controller + Mapper 생성
- `2-3` 주민번호 조회 → `/sms/ssn-search` Controller + Mapper 생성

### 2순위 — 운영 핵심 기능 (완전 신규)
- `3-3~3-5` SMS/LMS/알림톡 등록
- `3-6~3-8` SMS/LMS/알림톡 승인
- `4-2` 메세지 관리
- `1-3` 메세지조회

### 3순위 — 기존 화면 보정 필요
- `3-1` 발송대상관리 — 데이터 API 연동 확인
- `3-9` 발송내역조회(캠페인) — `SMS_HISTORY` 또는 `CAMPAIGN` 테이블 매핑 확인
- `4-5` 시간대별조회 — 실제 필드 요건 재검토

### 미정의 → 별도 기획 필요
- `1-1` SMS관리 시스템 안내
- `1-4` 사용자조회
- `1-5` MFA사용자 관리
- `4-3` 카카오템플릿관리
- `4-4` 광고성 메세지관리
- `6-1` 마케팅 철회 통계 ← 화면은 있으나 실제 DB 테이블 매핑 확인 필요

---

## 전체 진행률

| 카테고리 | 전체 | ✅ 완료 | ⚠️ 부분 | ❌ 미개발 |
|----------|------|---------|---------|---------|
| 기본메뉴 | 5 | 1 | 0 | 4 |
| SMS발송조회 | 3 | 1 | 2 | 0 |
| 캠페인 SMS | 11 | 1 | 2 | 8 |
| 시스템관리 | 5 | 1 | 1 | 3 |
| 계정관리 | 1 | 1 | 0 | 0 |
| 통계관리 | 1 | 1 | 0 | 0 |
| **합계** | **26** | **6 (23%)** | **5 (19%)** | **15 (58%)** |
