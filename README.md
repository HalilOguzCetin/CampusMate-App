# CampusMate

**Universite Kampusu Etkinlik Yonetim Uygulamasi**

---

## Proje Hakkinda

CampusMate, universite ogrencilerinin kampus etkinliklerini kolayca kesfedebildigi, etkinlik ekleyebildigi ve harita uzerinde goruntuleyebildigi bir Android mobil uygulamasidir. Manisa Celal Bayar Universitesi icin gelistirilmistir.

---

## Ozellikler

- **Kullanici Girisi & Kayit** — Firebase Authentication ile guvenli giris
- **Etkinlik Listeleme** — Onaylanmis etkinlikleri kategoriye gore filtrele
- **Etkinlik Ekleme** — Haritadan konum secerek etkinlik olustur
- **Yapay Zeka Analizi** — Etkinlikler yerel Naive Bayes motoruyla otomatik analiz edilir
- **Harita Gorunumu** — Etkinlikleri Google Maps uzerinde goruntle
- **Favoriler** — Begendigin etkinlikleri kaydet
- **Bildirimler** — Etkinlik hatirlatalari al
- **Admin Paneli** — Etkinlik onaylama ve kullanici yonetimi

---

## Yapay Zeka Motoru

Bu projenin en ozgun ozelligi, herhangi bir dis API kullanmadan tamamen cihaz uzerinde calisan yerel bir makine ogrenmesi motorudur.

- **Algoritma:** Laplace duzeltmeli Naive Bayes
- **Kategoriler:** Teknoloji, Sanat, Spor, Sosyal, Riskli
- **Cikti:** Kategori tahmini + 0-100 uygunluk skoru + karar gerekce
- **Avantaj:** Internet baglantisi gerektirmez, kullanici verisi ucuncu tarafla paylasilmaz

---

## Kullanilan Teknolojiler

| Teknoloji | Versiyon | Kullanim Amaci |
|---|---|---|
| Java | 11 | Ana programlama dili |
| Android SDK | API 24-35 | Android gelistirme platformu |
| Firebase Authentication | BOM 33.7.0 | Kullanici girisi ve kayit |
| Firebase Firestore | BOM 33.7.0 | Bulut veritabani |
| Google Maps SDK | 20.0.0 | Harita gorunturleme ve konum secimi |
| Material Design 3 | 1.10.0 | Kullanici arayuzu bilesenleri |
| Gradle Kotlin DSL | 9.2.1 | Build sistemi |

---

## Kurulum

### Gereksinimler
- Android Studio (Ladybug veya ustu)
- JDK 11
- Android cihaz veya emulator (API 24+)
- Firebase hesabi

### Adimlar

**1. Repoyu klonla**
```bash
git clone https://github.com/HalilOguzCetin/CampusMate-App.git
cd CampusMate-App
```

**2. Firebase projesi olustur**
- [Firebase Console](https://console.firebase.google.com) adresine git
- Yeni proje olustur
- Android uygulamasi ekle, package name: `com.example.campusmate`
- `google-services.json` dosyasini indir

**3. google-services.json dosyasini degistir**

Indirilen dosyayi `app/google-services.json` ile degistir.

**4. Firebase servislerini etkinlestir**
- Authentication → E-posta/Sifre yontemi
- Firestore Database → Test modunda olustur

**5. Google Maps API anahtarini guncelle**
- [Google Cloud Console](https://console.cloud.google.com) adresinden Maps SDK icin API anahtari al
- `AndroidManifest.xml` icerisindeki mevcut anahtari degistir

**6. Android Studio'da calistir**
- Projeyi ac, Gradle sync bekle, Run

