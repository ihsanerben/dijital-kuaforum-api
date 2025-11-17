// src/main/java/.../repository/UrunRepository.java

package com.dijitalkuaforum.dijitalkuaforum_backend.repository;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.Urun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrunRepository extends JpaRepository<Urun, Long> {
    // Aynı isimde ürün var mı kontrolü
    boolean existsByAd(String ad);
}