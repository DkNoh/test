package com.example.sms.controller.sms;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/campaign")
public class CampaignController {
    
    @GetMapping("/target-manage")
    public String targetManagePage() {
        return "campaign/target-manage";
    }
}
