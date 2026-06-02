package com.example.sms.service.system;

import com.example.sms.mapper.PrivacyAuditLogMapper;
import com.example.sms.vo.PrivacyAuditLogVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * [AuditLogService]
 * 개인정보 조회/다운로드 등의 행위에 대한 감사 로그(Audit Log)를 DB에 적재하는 서비스입니다.
 * 주로 @PrivacyLog AOP(Aspect) 내에서 자동으로 호출됩니다.
 * 
 * [사용법(Usage)]
 * 1. 수동으로 로그 기록이 필요할 때:
 *    PrivacyAuditLogVO logVO = PrivacyAuditLogVO.builder().action("...").build();
 *    auditLogService.saveLog(logVO);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final PrivacyAuditLogMapper privacyAuditLogMapper;

    public void saveLog(PrivacyAuditLogVO logVO) {
        try {
            privacyAuditLogMapper.insertAuditLog(logVO);
        } catch (Exception e) {
            // 감사 로그 저장이 실패하더라도 메인 비즈니스 로직(예: 엑셀 다운로드)은 정상 동작하도록 예외를 삼킵니다.
            log.error("개인정보 감사 로그 저장 중 오류 발생", e);
        }
    }
}