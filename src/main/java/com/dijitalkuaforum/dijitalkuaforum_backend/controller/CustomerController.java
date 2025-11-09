package com.dijitalkuaforum.dijitalkuaforum_backend.controller;

import com.dijitalkuaforum.dijitalkuaforum_backend.dto.LoginRequestDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.dto.SecuredCustomerRequestDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Barber;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Customer;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.AuthService;
import com.dijitalkuaforum.dijitalkuaforum_backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final AuthService authService; // Login kontrolü için

    private Optional<Barber> checkAuthentication(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        return authService.login(loginRequest);
    }

    // --- 1. CREATE (Oluşturma) ---
    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(@RequestBody SecuredCustomerRequestDTO request) {
        // Güvenlik Kontrolü: Her istekte kullanıcı adı/şifreyi kontrol et
        if (checkAuthentication(request.getUsername(), request.getPassword()).isEmpty()) {
            return new ResponseEntity<>("Yetkisiz Erişim: Geçerli kullanıcı adı ve şifre gereklidir.", HttpStatus.UNAUTHORIZED);
        }

        // Güvenlik başarılı, Müşteri işlemini yap
        Customer savedCustomer = customerService.save(request.getCustomer());
        return ResponseEntity.ok(savedCustomer);
    }

    // --- 2. READ ALL (Tümünü Listeleme) ---
    // GET istekleri body alamaz, bu yüzden güvenlik bilgilerini HTTP başlıklarından almalıyız.
    @GetMapping("/getAllCustomers")
    public ResponseEntity<?> getAllCustomers(
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password) {

        // Güvenlik Kontrolü
        if (checkAuthentication(username, password).isEmpty()) {
            return new ResponseEntity<>("Yetkisiz Erişim: Geçerli kullanıcı adı ve şifre gereklidir.", HttpStatus.UNAUTHORIZED);
        }

        // Güvenlik başarılı
        List<Customer> customers = customerService.findAll();
        return ResponseEntity.ok(customers);
    }

    // --- 3. DELETE (Silme) ---
    // DELETE istekleri body alabilir, ancak genellikle başlık kullanılır. Biz bu örnekte de başlık kullanalım.
    @DeleteMapping("/deleteCustomer/{id}")
    public ResponseEntity<?> deleteCustomer(
            @PathVariable Long id,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Password") String password) {

        // Güvenlik Kontrolü
        if (checkAuthentication(username, password).isEmpty()) {
            return new ResponseEntity<>("Yetkisiz Erişim: Geçerli kullanıcı adı ve şifre gereklidir.", HttpStatus.UNAUTHORIZED);
        }

        // Güvenlik başarılı, silme işlemini yap
        if (customerService.findById(id).isEmpty()) {
            return new ResponseEntity<>("Müşteri bulunamadı.", HttpStatus.NOT_FOUND);
        }
        customerService.deleteById(id);
        return ResponseEntity.ok("Müşteri başarıyla silindi.");
    }

    // PUT (Güncelleme) metodu da CREATE metodu gibi body içinde username/password alarak kolayca yapılabilir.
    // ...
}