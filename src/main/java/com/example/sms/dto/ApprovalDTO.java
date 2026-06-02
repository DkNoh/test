package com.example.sms.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * [결재 마스터 DTO]
 * TB_APPROVAL 테이블과 매핑되는 데이터 전송 객체입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalDTO {
    
    private String aprvId;       // 결재 ID (PK)
    private String aprvType;     // 결재 유형 (CAMPAIGN_SEND 등)
    private String aprvStatus;   // 결재 상태 (PENDING, APPROVED, REJECTED)
    
    private String refTable;     // 원본 업무 테이블명
    private String refPk;        // 원본 업무 테이블 PK
    
    private String reqTitle;     // 기안 제목
    private String reqUserId;    // 기안자 사번
    private LocalDateTime reqDt; // 기안 일시
    
    private String aprvUserId;   // 최종 처리자 사번
    private LocalDateTime aprvDt;// 최종 처리 일시
    
    // UI 표시용 (조인 결과)
    private String reqUserName;  // 기안자 이름
    private String aprvUserName; // 처리자 이름
}
