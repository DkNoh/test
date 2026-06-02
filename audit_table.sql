CREATE TABLE IF NOT EXISTS privacy_audit_log (
    log_id SERIAL PRIMARY KEY,
    executor_id VARCHAR(50) NOT NULL,
    executor_ip VARCHAR(50),
    request_url VARCHAR(200),
    action_type VARCHAR(100) NOT NULL,
    target_data VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE privacy_audit_log IS '개인정보/민감정보 조회 및 변경 감사 로그';
COMMENT ON COLUMN privacy_audit_log.log_id IS '로그 일련번호';
COMMENT ON COLUMN privacy_audit_log.executor_id IS '실행자(조회자) ID';
COMMENT ON COLUMN privacy_audit_log.executor_ip IS '실행자(조회자) IP 주소';
COMMENT ON COLUMN privacy_audit_log.request_url IS '접근 URL 경로';
COMMENT ON COLUMN privacy_audit_log.action_type IS '수행 업무 내용 (예: 개인정보 조회)';
COMMENT ON COLUMN privacy_audit_log.target_data IS '대상 데이터 정보 (예: 조회 조건 등)';
COMMENT ON COLUMN privacy_audit_log.created_at IS '발생일시';
