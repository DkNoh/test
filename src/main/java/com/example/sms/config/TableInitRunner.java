package com.example.sms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class TableInitRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // [자동화] SMS 스키마(사용자) 생성 및 권한 부여
        try {
            jdbcTemplate.execute("CREATE USER SMS IDENTIFIED BY 1234");
            jdbcTemplate.execute("GRANT DBA TO SMS");
        } catch (Exception e) {
            // 이미 존재할 경우 무시 (ORA-01920)
        }

        // [자동화] TB_DEP (부서) 테이블 생성
        String sqlDep = "BEGIN\n" +
                        "  EXECUTE IMMEDIATE 'CREATE TABLE TB_DEP (\n" +
                        "      DEPT_ID VARCHAR2(12) PRIMARY KEY,\n" +
                        "      DEP_NM VARCHAR2(20),\n" +
                        "      WRT_DTTM CHAR(14),\n" +
                        "      DEL_DTTM CHAR(14),\n" +
                        "      ACT_YN CHAR(1)\n" +
                        "  )';\n" +
                        "EXCEPTION\n" +
                        "  WHEN OTHERS THEN\n" +
                        "    IF SQLCODE != -955 THEN RAISE; END IF;\n" +
                        "END;";
        jdbcTemplate.execute(sqlDep);

        // [자동화] TB_EMP (사원) 테이블 생성
        String sqlEmp = "BEGIN\n" +
                        "  EXECUTE IMMEDIATE 'CREATE TABLE TB_EMP (\n" +
                        "      EMP_ID VARCHAR2(12) NOT NULL,\n" +
                        "      DEPT_ID VARCHAR2(12) NOT NULL,\n" +
                        "      EMP_PASS VARCHAR2(24) NULL,\n" +
                        "      EMP_NM VARCHAR2(20) NULL,\n" +
                        "      EMP_LEV CHAR(1) NULL,\n" +
                        "      REG_DTTM VARCHAR2(14) NULL,\n" +
                        "      MAX_SEND_CNT NUMBER NULL,\n" +
                        "      PERM_CPN CHAR(1) NULL,\n" +
                        "      PERM_SYS CHAR(1) NULL,\n" +
                        "      PERM_STA CHAR(1) NULL,\n" +
                        "      NOW_SEND_CNT NUMBER NULL,\n" +
                        "      LAST_SEND_DT CHAR(8) NULL,\n" +
                        "      LOGIN_FAIL_CNT NUMBER NULL,\n" +
                        "      EMP_PHONE VARCHAR2(15) NULL,\n" +
                        "      PERM_PSN CHAR(1) NULL,\n" +
                        "      PERM_AUT CHAR(1) NULL,\n" +
                        "      PERM_CPN_AGREE CHAR(1) NULL,\n" +
                        "      DEL_DTTM VARCHAR2(14) NULL,\n" +
                        "      ACT_YN CHAR(1) NULL,\n" +
                        "      LAST_LOGIN_DTTM VARCHAR2(14) NULL,\n" +
                        "      PASS_UPDATE_DTTM CHAR(14) NULL,\n" +
                        "      PASS_UPDATE_YN CHAR(1) DEFAULT ''N'' NOT NULL,\n" +
                        "      PERM_SND_CANCEL CHAR(1) NULL,\n" +
                        "      UPDATE_DTTM CHAR(14) NULL,\n" +
                        "      PERM_NOM CHAR(1) NULL,\n" +
                        "      PERM_NOM_AGREE CHAR(1) NULL,\n" +
                        "      PERM_MMS CHAR(1) NULL,\n" +
                        "      PERM_MMS_AGREE CHAR(1) NULL,\n" +
                        "      OLD_PWD1 VARCHAR2(24) NULL,\n" +
                        "      OLD_PWD2 VARCHAR2(24) NULL,\n" +
                        "      OLD_PWD3 VARCHAR2(24) NULL,\n" +
                        "      OLD_PWD4 VARCHAR2(24) NULL,\n" +
                        "      OLD_PWD5 VARCHAR2(24) NULL,\n" +
                        "      OLD_PWD6 VARCHAR2(24) NULL,\n" +
                        "      OLD_PWD7 VARCHAR2(24) NULL,\n" +
                        "      OLD_PWD8 VARCHAR2(24) NULL,\n" +
                        "      OLD_PWD9 VARCHAR2(24) NULL,\n" +
                        "      OLD_PWD10 VARCHAR2(24) NULL,\n" +
                        "      MFA_YN CHAR(1) NULL,\n" +
                        "      CONSTRAINT EMP_PK PRIMARY KEY (EMP_ID, DEPT_ID)\n" +
                        "  )';\n" +
                        "EXCEPTION\n" +
                        "  WHEN OTHERS THEN\n" +
                        "    IF SQLCODE != -955 THEN RAISE; END IF;\n" +
                        "END;";
        jdbcTemplate.execute(sqlEmp);

        // [자동화] admin 기초 데이터 인서트 (EMPLOYEE 테이블 기준)
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM EMPLOYEE WHERE EMP_ID = 'admin'", Integer.class);
            if (count != null && count == 0) {
                jdbcTemplate.update("INSERT INTO EMPLOYEE (EMP_ID, EMP_NAME, USE_YN, USER_ROLE, AUTH_READ, AUTH_APPROVE, AUTH_CAMPAIGN) " +
                                    "VALUES ('admin', '최고관리자', 'Y', 'ROLE_ADMIN', 'Y', 'Y', 'Y')");
            }
        } catch (Exception e) {}

        // [자동화] 부서 추가 더미 데이터 인서트
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM TB_DEP", Integer.class);
            if (count != null && count <= 1) { // D001 외 추가
                jdbcTemplate.update("INSERT INTO TB_DEP (DEPT_ID, DEP_NM, ACT_YN) VALUES ('D002', 'IT개발본부', 'Y')");
                jdbcTemplate.update("INSERT INTO TB_DEP (DEPT_ID, DEP_NM, ACT_YN) VALUES ('D003', '영업본부', 'Y')");
                jdbcTemplate.update("INSERT INTO TB_DEP (DEPT_ID, DEP_NM, ACT_YN) VALUES ('D004', '마케팅본부', 'Y')");
                jdbcTemplate.update("INSERT INTO TB_DEP (DEPT_ID, DEP_NM, ACT_YN) VALUES ('D005', 'HR본부', 'N')");
            }
        } catch (Exception e) {}

        // Oracle에서는 CREATE TABLE IF NOT EXISTS 대신 PL/SQL 예외 처리를 사용합니다. (에러코드 -955: 이미 존재함)
        String sql = "BEGIN\n" +
                     "  EXECUTE IMMEDIATE 'CREATE TABLE TB_PRIVACY_AUDIT_LOG (\n" +
                     "      log_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n" +
                     "      executor_id VARCHAR2(50) NOT NULL,\n" +
                     "      executor_ip VARCHAR2(50),\n" +
                     "      request_url VARCHAR2(200),\n" +
                     "      action_type VARCHAR2(100) NOT NULL,\n" +
                     "      target_data VARCHAR2(500),\n" +
                     "      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                     "  )';\n" +
                     "EXCEPTION\n" +
                     "  WHEN OTHERS THEN\n" +
                     "    IF SQLCODE != -955 THEN RAISE; END IF;\n" +
                     "END;";
        jdbcTemplate.execute(sql);

        String sqlFile = "BEGIN\n" +
                         "  EXECUTE IMMEDIATE 'CREATE TABLE TB_ATTACH_FILE (\n" +
                         "      file_id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n" +
                         "      original_name VARCHAR2(300) NOT NULL,\n" +
                         "      save_name VARCHAR2(300) NOT NULL,\n" +
                         "      file_path VARCHAR2(500) NOT NULL,\n" +
                         "      file_size NUMBER,\n" +
                         "      ext VARCHAR2(10),\n" +
                         "      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                         "  )';\n" +
                         "EXCEPTION\n" +
                         "  WHEN OTHERS THEN\n" +
                         "    IF SQLCODE != -955 THEN RAISE; END IF;\n" +
                         "END;";
        jdbcTemplate.execute(sqlFile);

        String sqlMessage = "BEGIN\n" +
                            "  EXECUTE IMMEDIATE 'CREATE TABLE TB_MESSAGE (\n" +
                            "      MSG_ID      NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,\n" +
                            "      DEPT_ID     VARCHAR2(50),\n" +
                            "      MSG_CODE    VARCHAR2(50) UNIQUE NOT NULL,\n" +
                            "      MSG_TYPE    VARCHAR2(20) DEFAULT ''NORMAL'' NOT NULL,\n" +
                            "      MSG_TITLE   VARCHAR2(200) NOT NULL,\n" +
                            "      MSG_CONTENT CLOB,\n" +
                            "      FORBID_YN   CHAR(1) DEFAULT ''N'',\n" +
                            "      SEND_POINT  VARCHAR2(100),\n" +
                            "      USE_YN      CHAR(1) DEFAULT ''Y'',\n" +
                            "      REG_ID      VARCHAR2(50),\n" +
                            "      REG_DT      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                            "      UPD_ID      VARCHAR2(50),\n" +
                            "      UPD_DT      TIMESTAMP\n" +
                            "  )';\n" +
                            "EXCEPTION\n" +
                            "  WHEN OTHERS THEN\n" +
                            "    IF SQLCODE != -955 THEN RAISE; END IF;\n" +
                            "END;";
        jdbcTemplate.execute(sqlMessage);

        // TB_BANK — 은행코드 마스터
        String sqlBank = "BEGIN\n" +
                         "  EXECUTE IMMEDIATE 'CREATE TABLE TB_BANK (\n" +
                         "      BANK_CD  VARCHAR2(10)  PRIMARY KEY,\n" +
                         "      BANK_NM  VARCHAR2(100) NOT NULL,\n" +
                         "      SWIFT_CD VARCHAR2(20),\n" +
                         "      USE_YN   CHAR(1) DEFAULT ''Y''\n" +
                         "  )';\n" +
                         "EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF;\n" +
                         "END;";
        jdbcTemplate.execute(sqlBank);

        // TB_BANK 샘플 데이터
        String[][] banks = {
            {"KB",    "국민은행",    "CZNBKRSE"},
            {"SHIN",  "신한은행",    "SHBKKRSE"},
            {"WOORI", "우리은행",    "HVBKKRSE"},
            {"HANA",  "하나은행",    "KOEXKRSE"},
            {"IBK",   "기업은행",    "IBKOKRSE"},
            {"KEB",   "KEB하나은행", "KOEXKRSE"},
            {"NH",    "농협은행",    "NACFKRSE"},
            {"SC",    "SC제일은행",  "SCBLKRSE"},
            {"CITI",  "씨티은행",    "CITIKRSX"},
            {"DGB",   "대구은행",    "DAEBKR22"},
            {"BNK",   "부산은행",    "PUSBKR2P"},
            {"JB",    "전북은행",    "JEONKRSE"},
            {"KDB",   "산업은행",    "KODBKRSE"},
            {"KIBO",  "기술보증기금", null}
        };
        for (String[] b : banks) {
            try {
                Integer cnt = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM TB_BANK WHERE BANK_CD = ?", Integer.class, b[0]);
                if (cnt == null || cnt == 0) {
                    jdbcTemplate.update(
                        "INSERT INTO TB_BANK (BANK_CD, BANK_NM, SWIFT_CD) VALUES (?, ?, ?)",
                        b[0], b[1], b[2]);
                }
            } catch (Exception ignored) {}
        }

        // TB_CUSTOMER_SSN — 주민번호/고객 조회용
        String sqlCustSsn = "BEGIN\n" +
                            "  EXECUTE IMMEDIATE 'CREATE TABLE TB_CUSTOMER_SSN (\n" +
                            "      PHONE_NO     VARCHAR2(20) PRIMARY KEY,\n" +
                            "      SSN_MASKED   VARCHAR2(14),\n" +
                            "      HOLDER_NAME  VARCHAR2(100),\n" +
                            "      DEPT_NM      VARCHAR2(100),\n" +
                            "      INTERMEDIARY VARCHAR2(100),\n" +
                            "      REG_DT       TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                            "  )';\n" +
                            "EXCEPTION WHEN OTHERS THEN IF SQLCODE != -955 THEN RAISE; END IF;\n" +
                            "END;";
        jdbcTemplate.execute(sqlCustSsn);

        // EMPLOYEE — MFA_YN 컬럼 추가 (USER_TAB_COLUMNS로 존재 확인 후 실행)
        addColumnIfNotExists("EMPLOYEE", "MFA_YN",       "CHAR(1) DEFAULT 'N'",   jdbcTemplate);

        // CAMPAIGN — 부가정보 컬럼 추가
        addColumnIfNotExists("CAMPAIGN", "REG_ID",       "VARCHAR2(50)",           jdbcTemplate);
        addColumnIfNotExists("CAMPAIGN", "TARGET_COUNT", "NUMBER DEFAULT 0",       jdbcTemplate);
        addColumnIfNotExists("CAMPAIGN", "APRV_USER",    "VARCHAR2(50)",           jdbcTemplate);
        addColumnIfNotExists("CAMPAIGN", "APRV_DT",      "DATE",                   jdbcTemplate);

        // 100건의 더미 데이터 삽입 (테이블이 존재할 때만)
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM TB_SMS_HISTORY", Integer.class);
            if (count != null && count < 10) {
                String[] types = {"SMS", "LMS", "ALIMTALK"};
                String[] statuses = {"SUCCESS", "FAIL", "PENDING"};
                String[] senders = {"admin", "user01", "user02"};
                String[] phones = {"010-1111-2222", "010-3333-4444", "010-5555-6666", "010-7777-8888"};
                
                for (int i = 4; i <= 103; i++) {
                    String msgId = "MSG-" + String.format("%04d", i);
                    String senderId = senders[i % 3];
                    String phone = phones[i % 4];
                    String type = types[i % 3];
                    String status = statuses[i % 3];
                    String dateStr = java.time.LocalDate.now().minusDays(i % 10).toString();
                    
                    try {
                        jdbcTemplate.update("INSERT INTO TB_SMS_HISTORY (MSG_ID, SENDER_ID, RECEIVER_PHONE, MSG_TYPE, SEND_STATUS, SEND_DT) VALUES (?, ?, ?, ?, ?, ?)",
                                msgId, senderId, phone, type, status, dateStr);
                    } catch (Exception e) {}
                }
            }
        } catch (Exception e) {
            // 테이블이 없는 경우 무시 (ORA-00942)
        }

        // [추가] 주소록 상세(연락처) 더미 데이터 50건 삽입
        try {
            // 깔끔한 멱등성을 위해 기존 연락처 데이터 완전 초기화
            try {
                jdbcTemplate.update("DELETE FROM TB_GROUP_CONTACT_MAP");
                jdbcTemplate.update("DELETE FROM TB_CONTACT");
            } catch (Exception ignored) {}

            // 1. 존재하는 최신 그룹 ID 5개 가져오기 (화면 1페이지에 표시되는 그룹들)
            java.util.List<String> groupIds = jdbcTemplate.queryForList("SELECT GROUP_ID FROM (SELECT GROUP_ID FROM TB_CONTACT_GROUP ORDER BY GROUP_ID DESC) WHERE ROWNUM <= 5", String.class);
                
                if (!groupIds.isEmpty()) {
                    for (int i = 1; i <= 50; i++) {
                        String contactNm = "테스트고객" + i;
                        String phoneNo = "010-" + String.format("%04d", i) + "-" + String.format("%04d", i);
                        String companyNm = "(주)테스트기업" + (i % 3 + 1);
                        String regId = "SYSTEM"; // ORA-01400 방지
                        
                        try {
                            // CONTACT_ID는 GENERATED ALWAYS AS IDENTITY 이므로 제외, REG_ID 추가
                            jdbcTemplate.update("INSERT INTO TB_CONTACT (CONTACT_NM, PHONE_NO, COMPANY_NM, REG_DT, REG_ID) VALUES (?, ?, ?, SYSDATE, ?)",
                                    contactNm, phoneNo, companyNm, regId);
                        } catch (Exception e) {
                            System.err.println("더미 연락처 INSERT 실패: " + e.getMessage());
                        }
                    }
                    
                    // DB에서 자동 생성된 CONTACT_ID들을 가져와서 매핑 테이블에 연결
                    java.util.List<String> insertedContactIds = jdbcTemplate.queryForList("SELECT CONTACT_ID FROM TB_CONTACT", String.class);
                    int idx = 0;
                    for (String cId : insertedContactIds) {
                        String groupId = groupIds.get(idx % groupIds.size());
                        try {
                            jdbcTemplate.update("INSERT INTO TB_GROUP_CONTACT_MAP (GROUP_ID, CONTACT_ID, REG_DT) VALUES (?, ?, SYSDATE)",
                                    groupId, cId);
                        } catch (Exception e) {}
                        idx++;
                    }
                    System.out.println(">>> 연락처 더미 데이터 50건 생성 완료!");
                }
        } catch (Exception e) {
            // 테이블이 없거나 구조가 다르면 스킵
        }

        // ===== 삭제된 메뉴 DB 정리 (재기동 시 자동 제거 — 멱등) =====
        deleteMenusByUrlPattern(jdbcTemplate, "/contact%");

        // 공지사항 중복 제거 — M_BASIC_NOTICE 만 남기고 나머지 삭제
        deleteMenusByUrlExcept(jdbcTemplate, "/basic/notice", "M_BASIC_NOTICE");

        // SMS발송조회 중복 제거 — M_SMS_SEND 만 남기고 나머지 삭제
        deleteMenusByNameExcept(jdbcTemplate, "SMS발송조회", "M_SMS_SEND");

        // 구형 메뉴명 기반 정리 (레거시 항목)
        deleteMenusByName(jdbcTemplate, "메세지 발송 이력 조회", "발송 이력(엔진 테스트)",
                          "SMS 발송조회"); // 띄어쓰기 있는 구형 항목

        // 오타 URL 메뉴 정리 (/basic/motice → /basic/currency 로 변경됨)
        deleteMenusByUrlPattern(jdbcTemplate, "/basic/motice");

        // ===== 전체 메뉴 구조 초기화 (멱등 — 이미 있으면 skip) =====

        // 1. 대메뉴 (최상위 — UP_MENU_CD·URL 없음) + SORT_ORD 강제 보정
        insertMenuIfNotExist(jdbcTemplate, "M_BASIC",    "기본메뉴",             null, null, 1);
        insertMenuIfNotExist(jdbcTemplate, "M_SMS_SEND", "SMS발송조회",          null, null, 2);
        insertMenuIfNotExist(jdbcTemplate, "M_CAMPAIGN", "캠페인 SMS",           null, null, 3);
        insertMenuIfNotExist(jdbcTemplate, "M_SYSTEM",   "시스템관리",           null, null, 4);
        insertMenuIfNotExist(jdbcTemplate, "M_ACCOUNT",  "시스템관리(계정관리)", null, null, 5);
        insertMenuIfNotExist(jdbcTemplate, "M_STAT",     "통계 관리",            null, null, 6);
        // SORT_ORD 불일치 방어 — 이미 존재해도 순서 강제 보정
        updateMenuSortOrd(jdbcTemplate, "M_BASIC",    1);
        updateMenuSortOrd(jdbcTemplate, "M_SMS_SEND", 2);
        updateMenuSortOrd(jdbcTemplate, "M_CAMPAIGN", 3);
        updateMenuSortOrd(jdbcTemplate, "M_SYSTEM",   4);
        updateMenuSortOrd(jdbcTemplate, "M_ACCOUNT",  5);
        updateMenuSortOrd(jdbcTemplate, "M_STAT",     6);

        // 2. 기본메뉴 소메뉴
        insertMenuIfNotExist(jdbcTemplate, "M_BASIC_NOTICE",    "공지사항",         "/basic/notice",    "M_BASIC", 1);
        insertMenuIfNotExist(jdbcTemplate, "M_BASIC_MSG",       "메세지조회",        "/basic/message",   "M_BASIC", 2);
        insertMenuIfNotExist(jdbcTemplate, "M_BASIC_MFA",       "MFA사용자 관리",    "/basic/mfa",       "M_BASIC", 3);
        insertMenuIfNotExist(jdbcTemplate, "M_BASIC_CURRENCY",  "환율조회",          "/basic/currency",  "M_BASIC", 4);

        // 3. SMS발송조회 소메뉴 (복원)
        insertMenuIfNotExist(jdbcTemplate, "M_SMS_HISTORY",   "발송내역조회",      "/sms/history",    "M_SMS_SEND", 1);
        insertMenuIfNotExist(jdbcTemplate, "M_SMS_CUSTOMER",  "고객별 조회",       "/sms/customer",   "M_SMS_SEND", 2);
        insertMenuIfNotExist(jdbcTemplate, "M_SMS_SSN",       "주민번호 조회",     "/sms/ssn",        "M_SMS_SEND", 3);

        // 4. 캠페인 SMS 소메뉴
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_TARGET",   "발송대상관리",      "/campaign/target-manage",     "M_CAMPAIGN",  1);
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_APRV",     "발송대상승인",      "/approval",                   "M_CAMPAIGN",  2);
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_SMS_REG",  "SMS등록",           "/campaign/sms/register",      "M_CAMPAIGN",  3);
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_LMS_REG",  "LMS등록",           "/campaign/lms/register",      "M_CAMPAIGN",  4);
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_ALM_REG",  "알림톡등록",        "/campaign/alimtalk/register", "M_CAMPAIGN",  5);
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_SMS_APR",  "SMS승인",           "/campaign/sms/approve",       "M_CAMPAIGN",  6);
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_LMS_APR",  "LMS승인",           "/campaign/lms/approve",       "M_CAMPAIGN",  7);
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_ALM_APR",  "알림톡승인",        "/campaign/alimtalk/approve",  "M_CAMPAIGN",  8);
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_SMS_HIST", "SMS발송내역조회",   "/sms/campaign",               "M_CAMPAIGN",  9);
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_LMS_HIST", "LMS발송내역조회",   "/sms/campaign-lms",           "M_CAMPAIGN", 10);
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_ALM_HIST", "알림톡발송내역조회","/sms/campaign-alimtalk",      "M_CAMPAIGN", 11);

        // 5. 시스템관리 소메뉴
        insertMenuIfNotExist(jdbcTemplate, "M_SYS_DEPT",      "부서 관리",         "/system/dept-manage",        "M_SYSTEM", 1);
        insertMenuIfNotExist(jdbcTemplate, "M_SYS_MSG",       "메세지 관리",        "/system/message-manage",     "M_SYSTEM", 2);
        insertMenuIfNotExist(jdbcTemplate, "M_SYS_KAKAO",     "카카오템플릿관리",   "/system/kakao-template",     "M_SYSTEM", 3);
        insertMenuIfNotExist(jdbcTemplate, "M_SYS_AD",        "광고성 메세지관리",  "/system/ad-message",         "M_SYSTEM", 4);
        insertMenuIfNotExist(jdbcTemplate, "M_SYS_TIME",      "시간대별조회",       "/sms/dept-stat",             "M_SYSTEM", 5);
        insertMenuIfNotExist(jdbcTemplate, "M_MENU_MNG",      "메뉴 및 권한 관리", "/system/menu-manage",        "M_SYSTEM", 6);

        // 6. 계정관리 소메뉴
        insertMenuIfNotExist(jdbcTemplate, "M_ACCT_USER",     "사용자관리",         "/account/user-manage",       "M_ACCOUNT", 1);

        // 7. 통계관리 소메뉴
        insertMenuIfNotExist(jdbcTemplate, "M_STAT_MKTG",     "마케팅 철회 통계",  "/statistics/marketing-optout","M_STAT",    1);
    }
    
    private void deleteMenus(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate, String... menuCds) {
        for (String cd : menuCds) {
            jdbcTemplate.update("DELETE FROM TB_MENU_AUTH WHERE MENU_CD = ?", cd);
            jdbcTemplate.update("DELETE FROM TB_MENU WHERE MENU_CD = ?", cd);
        }
    }

    private void deleteMenusByUrlPattern(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate, String urlPattern) {
        try {
            jdbcTemplate.update(
                "DELETE FROM TB_MENU_AUTH WHERE MENU_CD IN (SELECT MENU_CD FROM TB_MENU WHERE MENU_URL LIKE ?)", urlPattern);
            jdbcTemplate.update("DELETE FROM TB_MENU WHERE MENU_URL LIKE ?", urlPattern);
        } catch (Exception e) { /* URL 없으면 무시 */ }
    }

    private void updateMenuSortOrd(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate, String menuCd, int sortOrd) {
        jdbcTemplate.update("UPDATE TB_MENU SET SORT_ORD = ? WHERE MENU_CD = ?", sortOrd, menuCd);
    }

    private void deleteMenusByName(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate, String... menuNames) {
        for (String nm : menuNames) {
            try {
                jdbcTemplate.update(
                    "DELETE FROM TB_MENU_AUTH WHERE MENU_CD IN (SELECT MENU_CD FROM TB_MENU WHERE MENU_NM = ?)", nm);
                jdbcTemplate.update("DELETE FROM TB_MENU WHERE MENU_NM = ?", nm);
            } catch (Exception e) { /* 없으면 무시 */ }
        }
    }

    private void deleteMenusByNameExcept(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate, String menuNm, String keepMenuCd) {
        try {
            jdbcTemplate.update(
                "DELETE FROM TB_MENU_AUTH WHERE MENU_CD IN (SELECT MENU_CD FROM TB_MENU WHERE MENU_NM = ? AND MENU_CD != ?)",
                menuNm, keepMenuCd);
            jdbcTemplate.update(
                "DELETE FROM TB_MENU WHERE MENU_NM = ? AND MENU_CD != ?",
                menuNm, keepMenuCd);
        } catch (Exception e) { /* 없으면 무시 */ }
    }

    private void deleteMenusByUrlExcept(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate, String url, String keepMenuCd) {
        try {
            jdbcTemplate.update(
                "DELETE FROM TB_MENU_AUTH WHERE MENU_CD IN (SELECT MENU_CD FROM TB_MENU WHERE MENU_URL = ? AND MENU_CD != ?)",
                url, keepMenuCd);
            jdbcTemplate.update(
                "DELETE FROM TB_MENU WHERE MENU_URL = ? AND MENU_CD != ?",
                url, keepMenuCd);
        } catch (Exception e) { /* 없으면 무시 */ }
    }

    private void addColumnIfNotExists(String tableName, String columnName, String columnDef,
                                       org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        Integer cnt = jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM USER_TAB_COLUMNS WHERE TABLE_NAME = ? AND COLUMN_NAME = ?",
            Integer.class, tableName.toUpperCase(), columnName.toUpperCase());
        if (cnt == null || cnt == 0) {
            jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD (" + columnName + " " + columnDef + ")");
        }
    }

    private void insertMenuIfNotExist(org.springframework.jdbc.core.JdbcTemplate jdbcTemplate, String menuCd, String menuNm, String url, String upMenuCd, int sortOrd) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM TB_MENU WHERE MENU_CD = ?", Integer.class, menuCd);
        if (count != null && count == 0) {
            jdbcTemplate.update("INSERT INTO TB_MENU (MENU_CD, MENU_NM, MENU_URL, UP_MENU_CD, SORT_ORD, REG_ID, USE_YN) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    menuCd, menuNm, url, upMenuCd, sortOrd, "SYSTEM", "Y");
            jdbcTemplate.update("INSERT INTO TB_MENU_AUTH (MENU_CD, AUTH_CD, REG_ID) VALUES (?, ?, ?)",
                    menuCd, "ROLE_ADMIN", "SYSTEM");
        }
    }
}