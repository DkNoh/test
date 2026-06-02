package com.example.sms.dto.system;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MenuSearchRequestDTO extends PageRequestDTO {
    private String searchKeyword;
    private String useYn;
}
