package com.example.sms.mapper.system;

import com.example.sms.dto.system.MenuSearchRequestDTO;
import com.example.sms.dto.system.MenuSaveRequestDTO;
import com.example.sms.vo.system.MenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuManageMapper {
    int countMenus(MenuSearchRequestDTO request);
    List<MenuVO> selectMenus(MenuSearchRequestDTO request);
    
    // 트리 구성을 위한 전체 메뉴 조회
    List<MenuVO> selectAllMenus();
    
    // 특정 메뉴가 가진 권한 목록 (ROLE_ADMIN, ROLE_USER 등)
    List<MenuSaveRequestDTO.MenuAuthPermissionDTO> selectMenuAuths(@Param("menuCd") String menuCd);
    
    int insertMenu(MenuVO vo);
    int updateMenu(MenuVO vo);
    int deleteMenu(@Param("menuCd") String menuCd);
    int countMenusByUrlExcludeCd(@Param("menuUrl") String menuUrl, @Param("menuCd") String menuCd);
    
    // 중간 삽입 시 정렬 순서 밀어내기
    int shiftSortOrder(@Param("upMenuCd") String upMenuCd, @Param("sortOrd") int sortOrd, @Param("excludeMenuCd") String excludeMenuCd);
    
    int insertMenuAuth(@Param("menuCd") String menuCd,
                       @Param("auth") MenuSaveRequestDTO.MenuAuthPermissionDTO auth,
                       @Param("regId") String regId);
    int deleteMenuAuths(@Param("menuCd") String menuCd);
}
