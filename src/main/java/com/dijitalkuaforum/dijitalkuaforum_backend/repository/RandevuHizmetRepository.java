// src/main/java/.../repository/RandevuHizmetRepository.java
package com.dijitalkuaforum.dijitalkuaforum_backend.repository;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.RandevuHizmet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RandevuHizmetRepository extends JpaRepository<RandevuHizmet, Long> {
    // Randevuya ait hizmetleri listelemek için metotlar eklenebilir.
}