package com.example.sms.controller.sms;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {
    
    @GetMapping("/marketing-optout")
    public String marketingOptoutPage() {
        return "statistics/marketing-optout";
    }
}
