package com.gopitch.GoPitch.controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

    @GetMapping("/page")
    public String getUserPage() {
        
        return "userPage"; 
    }
    
}
