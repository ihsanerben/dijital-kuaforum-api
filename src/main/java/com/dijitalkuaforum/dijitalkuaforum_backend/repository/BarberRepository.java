package com.dijitalkuaforum.dijitalkuaforum_backend.repository;

import com.dijitalkuaforum.dijitalkuaforum_backend.model.Barber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarberRepository extends JpaRepository<Barber, Long> {

    Optional<Barber> findByUsername(String username);
}