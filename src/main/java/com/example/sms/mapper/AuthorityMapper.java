package com.example.sms.mapper;

import com.example.sms.dto.AuthorityDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

/**
 * [권한 관리 Mapper]
 */
@Mapper
public interface AuthorityMapper {
    long selectAuthorityListCount(com.example.sms.dto.AuthoritySearchRequestDTO request);
    List<AuthorityDTO> selectAuthorityList(com.example.sms.dto.AuthoritySearchRequestDTO request);
    int insertAuthority(AuthorityDTO dto);
    int deleteAuthority(String authCd);
}
