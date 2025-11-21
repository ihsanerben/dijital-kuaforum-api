package com.dijitalkuaforum.dijitalkuaforum_backend.service;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.Customer;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Randevu;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // --- ISSUE 3: CONTACT FORM EMAIL ---
    public void sendContactMessage(String name, String email, String messageBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(fromEmail); // Send to yourself (Admin)
        message.setSubject("Yeni İletişim Mesajı: " + name);
        message.setText("Gönderen: " + name + "\nEmail: " + email + "\n\nMesaj:\n" + messageBody);

        mailSender.send(message);
    }

    // --- ISSUE 2: APPOINTMENT NOTIFICATIONS ---

    // 1. Appointment Created (Pending)
    public void sendAppointmentCreated(Customer customer, Randevu randevu) {
        String subject = "Randevu Talebiniz Alındı - Dijital Kuaförüm";
        String text = "Sayın " + customer.getFullName() + ",\n\n" +
                "Randevu talebiniz alınmıştır ve onay beklemektedir.\n" +
                "Tarih: " + randevu.getStartTime() + "\n\n" +
                "Onaylandığında tekrar bilgilendirileceksiniz.";

        sendSimpleEmail(customer.getEmail(), subject, text);
    }

    // 2. Appointment Approved/Rejected
    public void sendAppointmentStatusUpdate(Customer customer, Randevu randevu) {
        String statusTr = randevu.getStatus().equals("ONAYLANDI") ? "ONAYLANDI" : "REDDEDİLDİ";
        String subject = "Randevu Durumu: " + statusTr;

        String text = "Sayın " + customer.getFullName() + ",\n\n" +
                "Randevunuzun durumu güncellendi: " + statusTr + "\n" +
                "Tarih: " + randevu.getStartTime();

        sendSimpleEmail(customer.getEmail(), subject, text);
    }

    // Helper method to avoid code duplication
    private void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Email gönderme hatası: " + e.getMessage());
            // Don't crash the app if email fails, just log it
        }
    }
}