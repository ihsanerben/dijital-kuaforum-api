package com.dijitalkuaforum.dijitalkuaforum_backend.controller;

import com.dijitalkuaforum.dijitalkuaforum_backend.service.EmailService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ContactRequest request) {
        emailService.sendContactMessage(request.getName(), request.getEmail(), request.getMessage());
        return ResponseEntity.ok("Mesajınız başarıyla gönderildi.");
    }

    @Data
    public static class ContactRequest {
        private String name;
        private String email;
        private String message;
    }
}