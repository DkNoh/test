package com.example.sms.dto;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthoritySearchRequestDTO extends PageRequestDTO {
    private String authCode;
    private String authName;
}
