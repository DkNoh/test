package com.example.sms.dto.contact;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContactGroupSearchRequestDTO extends PageRequestDTO {
    private String searchKeyword;
}