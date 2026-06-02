package com.example.sms.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * [권한 관리 DTO]
 * TB_AUTHORITY 테이블과 매핑됩니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDTO {
    private String authCd;       // 권한코드
    private String authNm;       // 권한명
    private String useYn;        // 사용여부
    private String regId;        // 등록자
    private LocalDateTime regDt; // 등록일시
    private String updId;        // 수정자
    private LocalDateTime updDt; // 수정일시
}
