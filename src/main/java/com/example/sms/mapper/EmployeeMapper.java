package com.example.sms.mapper;

import com.example.sms.dto.account.EmployeeSearchRequestDTO;
import com.example.sms.vo.EmployeeVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface EmployeeMapper {
    EmployeeVO findById(String empId);
    void updateLastLogin(EmployeeVO employee);
    
    List<EmployeeVO> selectEmployees(EmployeeSearchRequestDTO request);
    int countEmployees(EmployeeSearchRequestDTO request);
    
    void insertEmployee(EmployeeVO employee);
}
