package com.security.de.controller;

import com.security.de.service.KeyManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/keys")
public class KeyController {

    @GetMapping("/create")
    public String keyPage() {
        return "createKey";
    }

    @PostMapping("/create")
    public ModelAndView createKey(@RequestParam String senderId) {
        KeyManager.createKeyPair(senderId);
        ModelAndView modelAndView = new ModelAndView("completePage");
        modelAndView.addObject("message", senderId + "님의 키를 생성하였습니다.");
        return modelAndView;
    }
}
