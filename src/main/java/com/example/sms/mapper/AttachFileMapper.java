package com.example.sms.mapper;

import com.example.sms.vo.AttachFileVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AttachFileMapper {
    void insertAttachFile(AttachFileVO fileVO);
    AttachFileVO selectAttachFileById(Long fileId);
}