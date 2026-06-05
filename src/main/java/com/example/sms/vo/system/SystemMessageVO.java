package com.example.sms.vo.system;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemMessageVO {
    private long          rowNum;
    private Long          msgId;
    private String        deptId;
    private String        deptNm;      // TB_DEPT JOIN
    private String        msgCode;
    private String        msgType;     // NORMAL / KAKAO / AD
    private String        msgTitle;
    private String        msgContent;
    private String        forbidYn;
    private String        sendPoint;
    private String        useYn;
    private String        regId;
    private LocalDateTime regDt;
    private String        updId;
    private LocalDateTime updDt;
}
