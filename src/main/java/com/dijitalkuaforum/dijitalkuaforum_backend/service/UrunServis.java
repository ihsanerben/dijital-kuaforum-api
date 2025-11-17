// src/main/java/.../service/UrunServis.java

package com.dijitalkuaforum.dijitalkuaforum_backend.service;

import com.dijitalkuaforum.dijitalkuaforum_backend.exception.ResourceNotFoundException;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.DuplicateValueException;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Urun;
import com.dijitalkuaforum.dijitalkuaforum_backend.repository.UrunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UrunServis {

    private final UrunRepository urunRepository;

    // --- CRUD İŞLEMLERİ ---

    // 1. Ürün Ekle
    public Urun urunEkle(Urun urun) {
        if (urunRepository.existsByAd(urun.getAd())) {
            throw new DuplicateValueException("usta sikintiiiiii");
        }
        return urunRepository.save(urun);
    }

    // 2. Tüm Ürünleri Getir
    public List<Urun> tumUrunleriGetir() {
        return urunRepository.findAll();
    }

    // 3. ID ile Ürün Getir
    public Urun idIleUrunGetir(Long id) {
        return urunRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Urun", "id", id));
    }

    // 4. Ürün Güncelle
    public Urun urunGuncelle(Long id, Urun urunDetaylari) {
        Urun urun = idIleUrunGetir(id);

        // Duplicate kontrolü: Eğer yeni isim mevcut isimden farklıysa ve veritabanında varsa hata fırlat
        if (!urun.getAd().equals(urunDetaylari.getAd()) && urunRepository.existsByAd(urunDetaylari.getAd())) {
            throw new DuplicateValueException("bu urun ismi zaten mevcut kanka");
        }

        urun.setAd(urunDetaylari.getAd());
        urun.setFiyat(urunDetaylari.getFiyat());
        urun.setStokAdedi(urunDetaylari.getStokAdedi());
        urun.setTedarikci(urunDetaylari.getTedarikci());

        return urunRepository.save(urun);
    }

    // 5. Ürün Sil
    public void urunSil(Long id) {
        Urun urun = idIleUrunGetir(id);
        urunRepository.delete(urun);
    }
}