package com.example.sms.mapper;

import com.example.sms.vo.common.CommonCodeVO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CommonCodeMapper {
    List<CommonCodeVO> selectActiveDepartments();
}
