package com.ivan_degtev.documentaccounting2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "")
public class WelcomeController {

    @GetMapping()
    public String welcome() {
        return "redirect:/api/auth/sign-in";
    }
}