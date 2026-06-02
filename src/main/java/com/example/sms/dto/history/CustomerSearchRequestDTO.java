package com.example.sms.dto.history;

import com.example.sms.dto.common.PageRequestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.sms.config.ValidationCheck;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * [DTO 레이어] 고객별 SMS 이력 조회 요청 데이터
 * 
 * - 특징 1: 화면 특화 DTO. '고객별 조회' 화면에서 넘어오는 파라미터만 전담합니다.
 * - 특징 2: PageRequestDTO 상속. 페이징 처리(page, size, offset) 기능을 부모로부터 물려받아 사용합니다.
 * - 특징 3: 중앙 집중식 규약 기반 검증. jakarta.validation과 ValidationCheck 상수를 결합하여 
 *          컨트롤러 단에서 에러를 사전 차단(Fail-Fast)합니다.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomerSearchRequestDTO extends PageRequestDTO {

    // ValidationCheck의 규약 상수를 직접 참조하여 검증 조건을 부여합니다.
    // 값이 비어있으면 안 됨
    @NotBlank(message = "조회할 고객 ID는 필수 입력 항목입니다.")
    // 길이 제한: 5~15자 (예: CUST01)
    @Size(min = ValidationCheck.CUSTOMER_ID_MIN, max = ValidationCheck.CUSTOMER_ID_MAX, message = ValidationCheck.CUSTOMER_ID_MSG)
    private String customerId;

    // 검색 키워드는 필수값이 아니므로 @NotBlank는 생략하지만, 입력될 경우 최대 길이를 20자로 제한합니다.
    @Size(max = ValidationCheck.KEYWORD_MAX, message = ValidationCheck.KEYWORD_MSG)
    private String searchKeyword;
}
