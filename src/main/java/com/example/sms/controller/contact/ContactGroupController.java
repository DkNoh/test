package com.example.sms.controller.contact;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.contact.ContactGroupSearchRequestDTO;
import com.example.sms.dto.contact.ContactGroupVO;
import com.example.sms.service.contact.ContactGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contact/group")
@RequiredArgsConstructor
public class ContactGroupController {

    private final ContactGroupService service;

    /**
     * 화면(HTML) 반환
     */
    @GetMapping
    public String page() {
        return "contact/group-manage";
    }

    /**
     * TUI Grid 용 데이터 API (JSON 반환)
     */
    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<ContactGroupVO>>> getData(@ModelAttribute ContactGroupSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }

    /**
     * 단건 저장/수정 API
     */
    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> save(@RequestBody ContactGroupVO vo) {
        service.save(vo);
        return ResponseEntity.ok(ApiResponse.success("저장되었습니다."));
    }

    /**
     * 단건 삭제 API
     */
    @ResponseBody
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("삭제되었습니다."));
    }

    /**
     * 엑셀 파일 다운로드 요청 처리
     */
    @GetMapping("/excel")
    public void downloadExcel(@ModelAttribute ContactGroupSearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {
        service.downloadExcel(request, response);
    }
}