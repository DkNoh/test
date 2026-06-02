package com.example.sms.dto.history;

import com.example.sms.dto.common.PageRequestDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SMS 조회 전용 검색 파라미터
 *
 * ?page=1&size=10&startDate=2024-01-01&endDate=2024-12-31
 *  &receiverNo=010&sendType=SMS
 *
 * [Lombok] @Data: @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor를 한 번에 적용해주는 종합 선물 세트 어노테이션입니다.
 * [Lombok] @EqualsAndHashCode(callSuper = true): 상속받은 부모 클래스(PageRequestDTO)의 필드들까지 모두 포함해서 객체의 동일성(Equals)과 해시코드(HashCode)를 비교하라고 지시합니다.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SmsSearchRequestDTO extends PageRequestDTO {

    private String startDate;   // 발송일 시작 (yyyy-MM-dd)
    private String endDate;     // 발송일 종료 (yyyy-MM-dd)
    private String receiverNo;  // 수신번호 (부분검색)
    private String sendType;    // SMS | LMS | ALIMTALK | (전체)
}
