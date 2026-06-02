-- Create DEP table
CREATE TABLE DEP (
    DEP_ID VARCHAR2(50) PRIMARY KEY,
    DEP_NM VARCHAR2(100) NOT NULL,
    ACT_YN CHAR(1) DEFAULT 'Y'
);

INSERT INTO DEP (DEP_ID, DEP_NM, ACT_YN) VALUES ('D001', '개발팀', 'Y');
INSERT INTO DEP (DEP_ID, DEP_NM, ACT_YN) VALUES ('D030', '영업팀', 'Y');

-- Create employee table
CREATE TABLE employee (
    emp_id VARCHAR2(50) PRIMARY KEY,
    emp_name VARCHAR2(100) NOT NULL,
    dept_id VARCHAR2(50),
    use_yn CHAR(1) DEFAULT 'Y',
    user_role VARCHAR2(50) DEFAULT 'ROLE_USER',
    auth_read CHAR(1) DEFAULT 'Y',
    auth_approve CHAR(1) DEFAULT 'N',
    auth_campaign CHAR(1) DEFAULT 'N',
    last_login_ip VARCHAR2(50),
    last_login_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO employee (emp_id, emp_name, dept_id, use_yn, user_role, auth_read, auth_approve, auth_campaign) 
VALUES ('admin', '관리자', 'D001', 'Y', 'ROLE_ADMIN', 'Y', 'Y', 'Y');
INSERT INTO employee (emp_id, emp_name, dept_id, use_yn, user_role, auth_read, auth_approve, auth_campaign) 
VALUES ('user', '일반사용자', 'D030', 'Y', 'ROLE_USER', 'Y', 'N', 'N');

BEGIN
    FOR i IN 1..15 LOOP
        INSERT INTO employee (emp_id, emp_name, dept_id, use_yn) 
        VALUES (
            'testuser' || TO_CHAR(i, 'FM00'), 
            '테스터' || i, 
            CASE WHEN MOD(i, 2) = 0 THEN 'D001' ELSE 'D030' END, 
            CASE WHEN MOD(i, 5) = 0 THEN 'N' ELSE 'Y' END
        );
    END LOOP;
END;
/

-- Create sms_history table
CREATE TABLE sms_history (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    campaign_name VARCHAR2(200) NOT NULL,
    send_type VARCHAR2(20) DEFAULT 'SMS' NOT NULL,
    sender_no VARCHAR2(20) NOT NULL,
    receiver_no VARCHAR2(20) NOT NULL,
    message CLOB NOT NULL,
    send_status VARCHAR2(20) DEFAULT 'WAIT' NOT NULL,
    resend_yn CHAR(1) DEFAULT 'N' NOT NULL,
    result_code VARCHAR2(10),
    result_message VARCHAR2(200),
    reserved_at TIMESTAMP,
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_sms_history_sent_at ON sms_history (sent_at DESC);
CREATE INDEX idx_sms_history_receiver_no ON sms_history (receiver_no);
CREATE INDEX idx_sms_history_send_type ON sms_history (send_type);

-- Insert Dummy Data for sms_history
INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, sent_at) VALUES ('2024 신년 캠페인', 'SMS', '15881234', '01012345678', '[SC제일은행] 신년 이벤트 안내입니다. 자세한 내용은 홈페이지를 확인해 주세요.', 'SUCCESS', 'N', '0000', '정상', SYSDATE - 1);
INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, sent_at) VALUES ('2024 신년 캠페인', 'SMS', '15881234', '01023456789', '[SC제일은행] 신년 이벤트 안내입니다. 자세한 내용은 홈페이지를 확인해 주세요.', 'FAIL', 'Y', '1001', '수신거부', SYSDATE - 1);
INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, sent_at) VALUES ('적금 금리 안내', 'LMS', '15881234', '01034567890', '[SC제일은행] 고객님께 특별한 적금 상품을 안내드립니다. 연 5% 특판 적금 출시.', 'SUCCESS', 'N', '0000', '정상', SYSDATE - 2);
INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, sent_at) VALUES ('적금 금리 안내', 'LMS', '15881234', '01045678901', '[SC제일은행] 고객님께 특별한 적금 상품을 안내드립니다. 연 5% 특판 적금 출시.', 'FAIL', 'Y', '2001', '번호오류', SYSDATE - 2);
INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, sent_at) VALUES ('알림톡 승인 테스트', 'ALIMTALK', '15881234', '01056789012', '[SC제일은행] 고객님의 거래가 승인되었습니다. 승인번호: 123456', 'SUCCESS', 'N', '0000', '정상', SYSDATE - 3);
INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, sent_at) VALUES ('알림톡 승인 테스트', 'ALIMTALK', '15881234', '01067890123', '[SC제일은행] 고객님의 거래가 승인되었습니다. 승인번호: 234567', 'WAIT', 'N', NULL, NULL, NULL);
INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, sent_at) VALUES ('카드 혜택 안내', 'SMS', '15881234', '01078901234', '[SC제일은행] 카드 사용 혜택 안내. 이번 달 캐시백 3% 적용됩니다.', 'SUCCESS', 'N', '0000', '정상', SYSDATE - 5);
INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, sent_at) VALUES ('카드 혜택 안내', 'SMS', '15881234', '01089012345', '[SC제일은행] 카드 사용 혜택 안내. 이번 달 캐시백 3% 적용됩니다.', 'FAIL', 'Y', '2001', '번호오류', SYSDATE - 5);
INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, sent_at) VALUES ('대출 금리 인하 안내', 'LMS', '15881234', '01090123456', '[SC제일은행] 대출 금리가 인하되었습니다. 지금 바로 신청하세요.', 'SUCCESS', 'N', '0000', '정상', SYSDATE - 7);
INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, sent_at) VALUES ('대출 금리 인하 안내', 'LMS', '15881234', '01011112222', '[SC제일은행] 대출 금리가 인하되었습니다. 지금 바로 신청하세요.', 'SUCCESS', 'N', '0000', '정상', SYSDATE - 7);

COMMIT;
EXIT;
