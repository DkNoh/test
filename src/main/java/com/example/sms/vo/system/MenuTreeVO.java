package com.example.sms.vo.system;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class MenuTreeVO extends MenuVO {
    // TUI Tree Grid 하위 노드 (자식이 없을 경우 null이어야 그리드에서 '파일(소메뉴)' 아이콘으로 렌더링됨)
    private List<MenuTreeVO> _children = null;
    
    // TUI Tree Grid 상태 속성 (예: 펼침 상태)
    private Map<String, Object> _attributes = new HashMap<>();
    
    /**
     * [VO 생성자]
     * 기본적으로 트리를 화면에 그릴 때 모두 펼쳐진 상태(expanded: true)로 보여주기 위해 속성을 주입합니다.
     */
    public MenuTreeVO() {
        _attributes.put("expanded", true);
    }
}
