package com.dijitalkuaforum.dijitalkuaforum_backend.repository;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.Hizmet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HizmetRepository extends JpaRepository<Hizmet, Long> {
    boolean existsByAd(String ad);
}