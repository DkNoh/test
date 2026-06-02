package com.example.sms.service.system;

import com.example.sms.dto.AuthorityDTO;
import com.example.sms.mapper.AuthorityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.sms.dto.AuthoritySearchRequestDTO;
import com.example.sms.dto.common.PageResponseDTO;
import java.util.List;

/**
 * [권한 관리 비즈니스 서비스]
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityMapper authorityMapper;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public PageResponseDTO<AuthorityDTO> getAuthorityList(AuthoritySearchRequestDTO request) {
        request.validate();
        long totalCount = authorityMapper.selectAuthorityListCount(request);
        
        List<AuthorityDTO> list = java.util.Collections.emptyList();
        if (totalCount > 0) {
            list = authorityMapper.selectAuthorityList(request);
        }
        return PageResponseDTO.of(list, request, totalCount);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int createAuthority(AuthorityDTO dto) {
        return authorityMapper.insertAuthority(dto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteAuthority(String authCd) {
        return authorityMapper.deleteAuthority(authCd);
    }
}
