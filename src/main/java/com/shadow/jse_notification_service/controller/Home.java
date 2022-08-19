package com.shadow.jse_notification_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {

    @GetMapping
    public String uiDoc() {
        return "redirect:swagger-ui.html";
    }
}
