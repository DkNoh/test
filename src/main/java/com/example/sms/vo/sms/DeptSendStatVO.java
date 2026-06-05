package com.example.sms.vo.sms;

import lombok.Data;

@Data
public class DeptSendStatVO {
    private String deptId;
    private String deptNm;
    private Long sendingCount;
    private Long failCount;
    private Long successCount;
    private Long totalCount;
}
