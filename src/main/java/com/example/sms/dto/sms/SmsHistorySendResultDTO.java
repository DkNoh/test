package com.example.sms.dto.sms;

import com.example.sms.vo.sms.SmsHistoryVO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SmsHistorySendResultDTO {

    private LocalDateTime sentAt;
    private String receiverNo;
    private String sendStatus;

    public static SmsHistorySendResultDTO from(SmsHistoryVO vo) {
        return SmsHistorySendResultDTO.builder()
                .sentAt(vo.getSentAt())
                .receiverNo(vo.getReceiverNo())
                .sendStatus(vo.getSendStatus())
                .build();
    }
}
