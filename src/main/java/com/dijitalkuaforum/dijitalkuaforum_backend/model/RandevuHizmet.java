package com.dijitalkuaforum.dijitalkuaforum_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "randevu_hizmetleri")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RandevuHizmet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Randevu ilişkisi
    @ManyToOne
    @JoinColumn(name = "randevu_id", nullable = false)
    @JsonIgnore
    private Randevu randevu;

    // Hizmet ilişkisi
    @ManyToOne
    @JoinColumn(name = "hizmet_id", nullable = false)
    private Hizmet hizmet;

    // Not: Hizmetin adı, süresi ve fiyatı Hizmet tablosundan çekilecektir.
}