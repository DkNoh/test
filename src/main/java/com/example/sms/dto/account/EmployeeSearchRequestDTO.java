package com.example.sms.dto.account;

import com.example.sms.dto.common.PageRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeSearchRequestDTO extends PageRequestDTO {
    private String deptId;
    private String empName;
    private String empId;
}
