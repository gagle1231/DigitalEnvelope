package com.security.de.controller;

import com.security.de.service.SendDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.io.UnsupportedEncodingException;

@Controller
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final SendDataService sendDataService;

    @GetMapping("/send")
    public String sendPage() {
        return "sendpage";
    }

    @PostMapping("/send")
    public String sendData(@RequestParam String senderId, @RequestParam String receiverId, @RequestParam String contents, Model model) {
        sendDataService.sendMessage(senderId, receiverId, contents);
        String message = receiverId + "님께 암호화하여 메세지를 보냈습니다.";
        model.addAttribute("message", message);
        return "completePage";
    }

    @GetMapping("/read")
    public String readPage() {
        return "readMessage";
    }

    @PostMapping("/read")
    public String readMessage(@RequestParam String senderId, @RequestParam String receiverId, Model model) {
        String message = sendDataService.readMessage(senderId, receiverId);
        model.addAttribute("message", message);
        model.addAttribute("invalidSign", "Sign이 유효합니다.");
        return "readMessage";
    }
}
