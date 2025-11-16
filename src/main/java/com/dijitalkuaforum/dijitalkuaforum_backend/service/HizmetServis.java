package com.dijitalkuaforum.dijitalkuaforum_backend.service;

import com.dijitalkuaforum.dijitalkuaforum_backend.exception.DuplicateValueException;
import com.dijitalkuaforum.dijitalkuaforum_backend.exception.ResourceNotFoundException;
import com.dijitalkuaforum.dijitalkuaforum_backend.model.Hizmet;
import com.dijitalkuaforum.dijitalkuaforum_backend.repository.HizmetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HizmetServis {

    private final HizmetRepository hizmetRepository;

    @Autowired
    public HizmetServis(HizmetRepository hizmetRepository) {
        this.hizmetRepository = hizmetRepository;
    }

    // --- CRUD İŞLEMLERİ ---

    // 1. Yeni hizmet ekle (Admin)
    public Hizmet hizmetEkle(Hizmet hizmet) {

        // Kontrol: Aynı isimde hizmet var mı?
        if (hizmetRepository.existsByAd(hizmet.getAd())) {
            // Eğer varsa exception fırlat
            throw new DuplicateValueException("Girdiginiz hizmet adi(" + hizmet.getAd() + ") sistemde zaten mevcut.");
        }

        return hizmetRepository.save(hizmet);
    }

    // 2. Tüm hizmetleri listele (Müşteri ve Admin)
    public List<Hizmet> tumHizmetleriGetir() {
        return hizmetRepository.findAll();
    }

    // 3. Hizmet ID ile bul
    public Hizmet idIleHizmetGetir(Long id) {
        return hizmetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hizmet", "id", id));
    }

    // 4. Hizmet güncelle (Admin)
    // 4. Hizmet güncelle (Admin) - DUPLICATE KONTROLÜ DÜZELTİLDİ
    public Hizmet hizmetGuncelle(Long id, Hizmet hizmetDetaylari) {
        Hizmet hizmet = idIleHizmetGetir(id);

        // KRİTİK KONTROL: Yeni isim, mevcut isimden farklıysa VE veritabanında zaten varsa hata fırlat.
        // Eğer isim aynıysa, kontrolü atla.
        if (!hizmet.getAd().equals(hizmetDetaylari.getAd()) && hizmetRepository.existsByAd(hizmetDetaylari.getAd())) {
            throw new DuplicateValueException("usta hizmet isminda sikinti var. ");
        }

        hizmet.setAd(hizmetDetaylari.getAd());
        hizmet.setSureDakika(hizmetDetaylari.getSureDakika());
        hizmet.setFiyat(hizmetDetaylari.getFiyat());

        return hizmetRepository.save(hizmet);
    }

    // 5. Hizmet sil (Admin)
    public void hizmetSil(Long id) {
        Hizmet hizmet = idIleHizmetGetir(id);
        hizmetRepository.delete(hizmet);
    }
}