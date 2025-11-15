package com.dijitalkuaforum.dijitalkuaforum_backend.controller;

import com.dijitalkuaforum.dijitalkuaforum_backend.dto.LoginRequestDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.dto.ResponseRequestDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.dto.SecuredCustomerRequestDTO;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.DuplicateValueException;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.ResourceNotFoundException;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.UnauthorizedException;
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
            throw new UnauthorizedException();
        }

        // Güvenlik başarılı, Müşteri işlemini yap
        Customer savedCustomer = customerService.save(request.getCustomer());
        return ResponseEntity.ok(savedCustomer);
    }

    @PutMapping("/updateCustomer/{id}")
    public ResponseEntity<?> putCustomer(@RequestHeader("Username") String username,
                                         @RequestHeader("Password") String password,
                                         @PathVariable Long id,
                                         @RequestBody Customer updatedCustomer) {
        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        Customer resultCustomer = customerService.updateCustomer(id, updatedCustomer);

        return ResponseEntity.ok("Musterimiz guncellendi" + resultCustomer);
    }

    // --- 2. READ ALL (Tümünü Listeleme) ---
    // GET istekleri body alamaz, bu yüzden güvenlik bilgilerini HTTP başlıklarından almalıyız.
    @GetMapping("/getAllCustomers")
    public ResponseEntity<?> getAllCustomers(
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password) {

        // Güvenlik Kontrolü
        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
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
            @RequestHeader("Username") String username,
            @RequestHeader("Password") String password) {

        // Güvenlik Kontrolü
        if (checkAuthentication(username, password).isEmpty()) {
            throw new UnauthorizedException();
        }

        // Güvenlik başarılı, silme işlemini yap
        if (customerService.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("musteri", "id", id);
        }

        Customer customer = customerService.findById(id).get();
        customerService.deleteById(id);
        return ResponseEntity.ok("Müşterimiz " + customer.getFullName() + " sistemden başarıyla silindi.");
    }

    // PUT (Güncelleme) metodu da CREATE metodu gibi body içinde username/password alarak kolayca yapılabilir.
    // ...
}