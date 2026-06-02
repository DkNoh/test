-- ============================================================
-- SMS 발송이력 테이블
-- ============================================================
CREATE TABLE IF NOT EXISTS sms_history (
    id              BIGSERIAL       PRIMARY KEY,
    campaign_name   VARCHAR(200)    NOT NULL,
    send_type       VARCHAR(20)     NOT NULL DEFAULT 'SMS',   -- SMS | LMS | ALIMTALK
    sender_no       VARCHAR(20)     NOT NULL,
    receiver_no     VARCHAR(20)     NOT NULL,
    message         TEXT            NOT NULL,
    send_status     VARCHAR(20)     NOT NULL DEFAULT 'WAIT',  -- WAIT | SUCCESS | FAIL
    resend_yn       CHAR(1)         NOT NULL DEFAULT 'N',     -- 재발송여부 Y|N
    result_code     VARCHAR(10),
    result_message  VARCHAR(200),
    reserved_at     TIMESTAMP,
    sent_at         TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE  sms_history IS 'SMS 발송이력';
COMMENT ON COLUMN sms_history.send_type   IS '발송유형 SMS|LMS|ALIMTALK';
COMMENT ON COLUMN sms_history.send_status IS '발송상태 WAIT|SUCCESS|FAIL';
COMMENT ON COLUMN sms_history.resend_yn   IS '재발송여부 Y|N';

CREATE INDEX IF NOT EXISTS idx_sms_history_sent_at     ON sms_history (sent_at DESC);
CREATE INDEX IF NOT EXISTS idx_sms_history_receiver_no ON sms_history (receiver_no);
CREATE INDEX IF NOT EXISTS idx_sms_history_send_type   ON sms_history (send_type);

-- ============================================================
-- 테스트 데이터 (재발송여부 포함)
-- ============================================================
INSERT INTO sms_history (campaign_name, send_type, sender_no, receiver_no, message, send_status, resend_yn, result_code, result_message, sent_at) VALUES
('2024 신년 캠페인',    'SMS',      '15881234', '01012345678', '[SC제일은행] 신년 이벤트 안내입니다. 자세한 내용은 홈페이지를 확인해 주세요.',   'SUCCESS', 'N', '0000', '정상',    NOW() - INTERVAL '1 day'),
('2024 신년 캠페인',    'SMS',      '15881234', '01023456789', '[SC제일은행] 신년 이벤트 안내입니다. 자세한 내용은 홈페이지를 확인해 주세요.',   'FAIL',    'Y', '1001', '수신거부', NOW() - INTERVAL '1 day'),
('적금 금리 안내',      'LMS',      '15881234', '01034567890', '[SC제일은행] 고객님께 특별한 적금 상품을 안내드립니다. 연 5% 특판 적금 출시.',    'SUCCESS', 'N', '0000', '정상',    NOW() - INTERVAL '2 days'),
('적금 금리 안내',      'LMS',      '15881234', '01045678901', '[SC제일은행] 고객님께 특별한 적금 상품을 안내드립니다. 연 5% 특판 적금 출시.',    'FAIL',    'Y', '2001', '번호오류', NOW() - INTERVAL '2 days'),
('알림톡 승인 테스트',  'ALIMTALK', '15881234', '01056789012', '[SC제일은행] 고객님의 거래가 승인되었습니다. 승인번호: 123456',                  'SUCCESS', 'N', '0000', '정상',    NOW() - INTERVAL '3 days'),
('알림톡 승인 테스트',  'ALIMTALK', '15881234', '01067890123', '[SC제일은행] 고객님의 거래가 승인되었습니다. 승인번호: 234567',                  'WAIT',    'N', NULL,   NULL,      NULL),
('카드 혜택 안내',      'SMS',      '15881234', '01078901234', '[SC제일은행] 카드 사용 혜택 안내. 이번 달 캐시백 3% 적용됩니다.',                'SUCCESS', 'N', '0000', '정상',    NOW() - INTERVAL '5 days'),
('카드 혜택 안내',      'SMS',      '15881234', '01089012345', '[SC제일은행] 카드 사용 혜택 안내. 이번 달 캐시백 3% 적용됩니다.',                'FAIL',    'Y', '2001', '번호오류', NOW() - INTERVAL '5 days'),
('대출 금리 인하 안내', 'LMS',      '15881234', '01090123456', '[SC제일은행] 대출 금리가 인하되었습니다. 지금 바로 신청하세요.',                  'SUCCESS', 'N', '0000', '정상',    NOW() - INTERVAL '7 days'),
('대출 금리 인하 안내', 'LMS',      '15881234', '01011112222', '[SC제일은행] 대출 금리가 인하되었습니다. 지금 바로 신청하세요.',                  'SUCCESS', 'N', '0000', '정상',    NOW() - INTERVAL '7 days');
