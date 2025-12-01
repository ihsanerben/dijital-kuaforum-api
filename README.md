# 💈 Dijital Kuaförüm - REST API (Backend)

Bu proje, **Dijital Kuaförüm** salon yönetim sisteminin sunucu tarafını (Backend) oluşturur. Spring Boot kullanılarak geliştirilmiş olup, randevu mantığı, yetkilendirme, veritabanı yönetimi ve iş zekası (istatistikler) süreçlerini yönetir.

## 🏗️ Mimari ve Özellikler

Proje, **Katmanlı Mimari (Layered Architecture)** prensiplerine uygun olarak Controller, Service, Repository ve Model katmanlarından oluşur.

### 🔑 Temel Yetenekler
* **Randevu Motoru:**
    * Dinamik slot hesaplama (Hizmet süresine göre uygun zaman aralıklarını belirleme).
    * Akıllı çakışma kontrolü (Conflict detection logic).
    * Admin ve Müşteri için farklı randevu oluşturma akışları.
* **Kullanıcı Yönetimi & Güvenlik:**
    * Özel Yetkilendirme (Custom Auth) mekanizması (Admin ve Customer ayrımı).
    * Müşteri kayıt ve doğrulama altyapısı.
* **Veri Yönetimi (CRUD):**
    * **Hizmetler:** Kuaför hizmetlerinin (fiyat, süre) yönetimi.
    * **Ürünler:** Stok takibi ve ürün yönetimi.
    * **Müşteriler:** Detaylı müşteri veritabanı.
* **Raporlama & Analitik:**
    * Toplam gelir hesaplama.
    * Hizmet dağılımı analizi (Hangi hizmetin ne kadar tercih edildiği).
    * Dinamik tarih aralığına göre raporlama.

## 🛠️ Teknolojiler

* **Dil:** Java 17+
* **Framework:** Spring Boot 3.x (Web, Data JPA)
* **Veritabanı:** PostgreSQL
* **ORM:** Hibernate
* **Araçlar:** Lombok, Maven

## 🗄️ Veritabanı Modelleri (Entities)

* `Randevu`: Randevu zamanı, durumu ve müşteri ilişkisi.
* `Customer`: Müşteri iletişim bilgileri.
* `Hizmet`: Hizmet adı, süresi ve ücreti.
* `RandevuHizmet`: Randevu ve Hizmet arasındaki çoka-çok ilişkiyi yöneten ara tablo.
* `Urun`: Dükkan içi satılan ürünler ve stok bilgisi.
* `Barber`: Yönetici/Kuaför bilgileri.

## ⚙️ Kurulum ve Çalıştırma

1.  **Veritabanı Ayarları:**
    PostgreSQL veritabanınızı oluşturun ve `src/main/resources/application.properties` dosyasındaki ayarları güncelleyin:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/dijitalkuaforum_db
    spring.datasource.username=postgres
    spring.datasource.password=sifreniz
    spring.jpa.hibernate.ddl-auto=update
    ```

2.  **Projeyi Derleyin:**
    ```bash
    mvn clean install
    ```

3.  **Uygulamayı Başlatın:**
    ```bash
    mvn spring-boot:run
    ```
    
## 📡 API Endpoints (Örnekler)

* `POST /api/randevular/olustur` - Yeni randevu talebi.
* `GET /api/randevular/takvim` - Belirli bir tarihteki randevuları getirir.
* `GET /api/hizmetler/public/getAll` - Tüm hizmetleri listeler.
* `GET /api/randevular/admin/istatistik` - Admin paneli istatistiklerini hesaplar.

---
**Geliştirici:** İhsan Eren Erben
