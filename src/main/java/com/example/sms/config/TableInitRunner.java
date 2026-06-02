package com.example.sms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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

        // [자동화] SMS.DEP (부서) 테이블 생성
        String sqlDep = "BEGIN\n" +
                        "  EXECUTE IMMEDIATE 'CREATE TABLE SMS.DEP (\n" +
                        "      DEP_ID VARCHAR2(12) PRIMARY KEY,\n" +
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

        // [자동화] SMS.EMP (사원) 테이블 생성
        String sqlEmp = "BEGIN\n" +
                        "  EXECUTE IMMEDIATE 'CREATE TABLE SMS.EMP (\n" +
                        "      EMP_ID VARCHAR2(12) NOT NULL,\n" +
                        "      DEP_ID VARCHAR2(12) NOT NULL,\n" +
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
                        "      CONSTRAINT EMP_PK PRIMARY KEY (EMP_ID, DEP_ID)\n" +
                        "  )';\n" +
                        "EXCEPTION\n" +
                        "  WHEN OTHERS THEN\n" +
                        "    IF SQLCODE != -955 THEN RAISE; END IF;\n" +
                        "END;";
        jdbcTemplate.execute(sqlEmp);

        // [자동화] admin 기초 데이터 인서트 (최초 로그인 지원용)
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM SMS.EMP WHERE EMP_ID = 'admin'", Integer.class);
            if (count != null && count == 0) {
                jdbcTemplate.update("INSERT INTO SMS.DEP (DEP_ID, DEP_NM, ACT_YN) VALUES ('D001', '시스템관리부', 'Y')");
                jdbcTemplate.update("INSERT INTO SMS.EMP (EMP_ID, DEP_ID, EMP_NM, EMP_LEV, PERM_SYS, PERM_CPN, PERM_STA, ACT_YN, REG_DTTM) " +
                                    "VALUES ('admin', 'D001', '최고관리자', '9', 'Y', 'Y', 'Y', 'Y', TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'))");
            }
        } catch (Exception e) {}

        // [자동화] 부서 추가 더미 데이터 인서트
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM SMS.DEP", Integer.class);
            if (count != null && count <= 1) { // D001 외 추가
                jdbcTemplate.update("INSERT INTO SMS.DEP (DEP_ID, DEP_NM, ACT_YN) VALUES ('D002', 'IT개발본부', 'Y')");
                jdbcTemplate.update("INSERT INTO SMS.DEP (DEP_ID, DEP_NM, ACT_YN) VALUES ('D003', '영업본부', 'Y')");
                jdbcTemplate.update("INSERT INTO SMS.DEP (DEP_ID, DEP_NM, ACT_YN) VALUES ('D004', '마케팅본부', 'Y')");
                jdbcTemplate.update("INSERT INTO SMS.DEP (DEP_ID, DEP_NM, ACT_YN) VALUES ('D005', 'HR본부', 'N')");
            }
        } catch (Exception e) {}

        // Oracle에서는 CREATE TABLE IF NOT EXISTS 대신 PL/SQL 예외 처리를 사용합니다. (에러코드 -955: 이미 존재함)
        String sql = "BEGIN\n" +
                     "  EXECUTE IMMEDIATE 'CREATE TABLE privacy_audit_log (\n" +
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
                         "  EXECUTE IMMEDIATE 'CREATE TABLE attach_file (\n" +
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

        // 스캐폴드 엔진 테스트용 메뉴 강제 삽입 (403 에러 방지)
        insertMenuIfNotExist(jdbcTemplate, "M_CAMP_DEMO", "캠페인 조회 (데모)", "/sms/campaign", "M_CAMPAIGN", 7);
        insertMenuIfNotExist(jdbcTemplate, "M_MENU_MNG", "메뉴 및 권한 관리", "/system/menu-manage", "M_SYSTEM", 8);
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