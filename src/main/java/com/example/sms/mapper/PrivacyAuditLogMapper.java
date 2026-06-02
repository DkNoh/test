package com.example.sms.mapper;

import com.example.sms.vo.system.PrivacyAuditLogVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PrivacyAuditLogMapper {
    void insertAuditLog(PrivacyAuditLogVO logVO);
}