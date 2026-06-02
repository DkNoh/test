package com.example.sms;

import com.example.sms.dto.ApprovalDTO;
import com.example.sms.mapper.ApprovalMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class DataInsertTest {

    @Autowired
    private ApprovalMapper approvalMapper;

    @Test
    public void insertDummyApprovalData() {
        String[] types = {"SMS", "LMS", "ALIMTALK"};
        String[] statuses = {"PENDING", "APPROVED", "REJECTED"};
        String[] users = {"user01", "user02", "admin"};

        // 15 records
        for (int i = 1; i <= 15; i++) {
            ApprovalDTO dto = ApprovalDTO.builder()
                .aprvId("APR-TEST-" + String.format("%03d", i))
                .aprvType(types[i % 3])
                .aprvStatus(statuses[i % 3])
                .refTable("TB_CAMPAIGN")
                .refPk("CAMP-" + i)
                .reqTitle("테스트 캠페인 결재 요청 " + i)
                .reqUserId(users[i % 3])
                .aprvUserId("admin")
                .build();
                
            try {
                approvalMapper.insertApproval(dto);
            } catch (Exception e) {
                // Ignore if duplicate
                System.out.println("Insert skipped: " + e.getMessage());
            }
        }
        System.out.println("Successfully inserted 15 dummy records!");
    }

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Test
    public void insertSmsHistoryMenu() {
        try {
            // M_HISTORY 밑에 붙입니다 (M_HIST_SCAFFOLD)
            jdbcTemplate.update("INSERT INTO TB_MENU (MENU_CD, MENU_NM, MENU_URL, UP_MENU_CD, SORT_ORD, REG_ID, USE_YN) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    "M_HIST_SCAFF", "메시지 발송 이력 조회", "/sms/history", "M_HISTORY", 5, "SYSTEM", "Y");
            jdbcTemplate.update("INSERT INTO TB_MENU_AUTH (MENU_CD, AUTH_CD, REG_ID) VALUES (?, ?, ?)",
                    "M_HIST_SCAFF", "ROLE_ADMIN", "SYSTEM");
            System.out.println("====== [성공] 메뉴가 정상적으로 DB에 등록되었습니다! ======");
        } catch (Exception e) {
            System.out.println("====== [알림] 메뉴가 이미 등록되어 있거나 오류가 발생했습니다: " + e.getMessage() + " ======");
        }
    }



    @Test
    public void insert100SmsHistory() {
        String[] types = {"SMS", "LMS", "ALIMTALK"};
        String[] statuses = {"SUCCESS", "FAIL", "PENDING"};
        String[] senders = {"admin", "user01", "user02"};
        String[] phones = {"010-1111-2222", "010-3333-4444", "010-5555-6666", "010-7777-8888"};

        int successCount = 0;
        for (int i = 4; i <= 103; i++) {
            String msgId = "MSG-" + String.format("%04d", i);
            String senderId = senders[i % 3];
            String phone = phones[i % 4];
            String type = types[i % 3];
            String status = statuses[i % 3];
            
            // 최근 10일 전까지의 날짜로 분산
            String dateStr = java.time.LocalDate.now().minusDays(i % 10).toString();

            try {
                jdbcTemplate.update("INSERT INTO TB_SMS_HISTORY (MSG_ID, SENDER_ID, RECEIVER_PHONE, MSG_TYPE, SEND_STATUS, SEND_DT) VALUES (?, ?, ?, ?, ?, ?)",
                        msgId, senderId, phone, type, status, dateStr);
                successCount++;
            } catch (Exception e) {
                // Ignore duplicates
            }
        }
        System.out.println("====== [성공] 총 " + successCount + "건의 더미 데이터를 추가했습니다! ======");
    }
}
