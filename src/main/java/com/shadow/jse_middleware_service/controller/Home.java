package com.shadow.jse_middleware_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {

    @GetMapping
    public String uiDoc() {
        return "redirect:swagger-ui/index.html";
    }
}
