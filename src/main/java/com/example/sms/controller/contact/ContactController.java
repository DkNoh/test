package com.example.sms.controller.contact;

import com.example.sms.dto.common.ApiResponse;
import com.example.sms.dto.common.PageResponseDTO;
import com.example.sms.dto.contact.ContactSearchRequestDTO;
import com.example.sms.dto.contact.ContactVO;
import com.example.sms.service.contact.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contact/item")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService service;

    @GetMapping
    public String page() {
        return "contact/item-manage";
    }

    @ResponseBody
    @GetMapping("/data")
    public ResponseEntity<ApiResponse<PageResponseDTO<ContactVO>>> getData(@ModelAttribute ContactSearchRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(service.search(request)));
    }

    @ResponseBody
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> save(@RequestBody ContactVO vo) {
        service.save(vo);
        return ResponseEntity.ok(ApiResponse.success("저장되었습니다."));
    }

    @ResponseBody
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success("삭제되었습니다."));
    }

    @GetMapping("/excel")
    public void downloadExcel(@ModelAttribute ContactSearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {
        service.downloadExcel(request, response);
    }
}
