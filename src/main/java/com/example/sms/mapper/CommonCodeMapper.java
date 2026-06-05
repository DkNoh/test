package com.example.sms.mapper;

import com.example.sms.vo.common.CommonCodeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommonCodeMapper {
    List<CommonCodeVO> selectActiveDepartments();
    List<CommonCodeVO> selectMessagesByDept(@Param("deptId") String deptId);
    List<CommonCodeVO> selectBanksByKeyword(@Param("keyword") String keyword);
}
