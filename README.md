# 📷 Axl Rent - Aplikasi Penyewaan Kamera Android

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Android SDK](https://img.shields.io/badge/Min%20SDK-26%20%7C%20Target%20SDK-35-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)

**Axl Rent** adalah aplikasi penyewaan kamera berbasis **Android Native** modern yang dibangun menggunakan **Jetpack Compose** dan **Firebase**. Aplikasi ini dirancang untuk menyelesaikan kesulitan fotografer pemula, kreator konten, maupun masyarakat umum dalam mencari, memilih, dan menyewa perangkat kamera serta aksesorinya secara cepat, transparan, dan terintegrasi secara digital.

---

## 📱 Tampilan Antarmuka (UI Preview)

<div align="center">
  <table>
    <tr>
      <td align="center"><b>Onboarding</b></td>
      <td align="center"><b>Katalog Kamera (Home)</b></td>
      <td align="center"><b>Detail Spesifikasi</b></td>
      <td align="center"><b>Pemesanan (Booking)</b></td>
    </tr>
    <tr>
      <td><img src="ui/Onboarding%20Screen%20(Refined).png" width="180"/></td>
      <td><img src="ui/Home%20Screen%20(Refined).png" width="180"/></td>
      <td><img src="ui/Detail%20Kamera%20(Refined).png" width="180"/></td>
      <td><img src="ui/Booking%20Screen%20(Refined).png" width="180"/></td>
    </tr>
    <tr>
      <td align="center"><b>Pembayaran</b></td>
      <td align="center"><b>Pembayaran Sukses</b></td>
      <td align="center"><b>Riwayat Pesanan</b></td>
      <td align="center"><b>Profil Pengguna</b></td>
    </tr>
    <tr>
      <td><img src="ui/Payment%20Screen%20(Refined).png" width="180"/></td>
      <td><img src="ui/Payment%20Success%20Screen%20(Refined).png" width="180"/></td>
      <td><img src="ui/Riwayat%20Pesanan%20(Refined).png" width="180"/></td>
      <td><img src="ui/Profil%20Pengguna%20(Refined).png" width="180"/></td>
    </tr>
  </table>
</div>

---

## ✨ Fitur Utama

* **🔐 Autentikasi Pengguna**: Registrasi akun baru, login dengan email/password, terintegrasi aman menggunakan **Firebase Authentication**.
* **🔍 Katalog & Pencarian Kamera**: Eksplorasi berbagai jenis kamera (DSLR, Mirrorless, Action Cam) lengkap dengan fitur pencarian dan filter kategori.
* **📋 Detail & Spesifikasi Lengkap**: Informasi mendalam seputar sensor kamera, resolusi, lensa bawaan, kondisi ketersediaan, serta ulasan rating.
* **📅 Sistem Booking & Kalkulasi Otomatis**: Pemilihan durasi sewa, tanggal mulai dan selesai, dengan perhitungan biaya sewa harian otomatis.
* **💳 Simulasi Pembayaran & QRIS**: Pilihan metode pembayaran modern (Transfer Bank & QRIS) dengan konfirmasi pembayaran langsung.
* **📜 Riwayat Transaksi Realtime**: Pantau status sewa dan riwayat transaksi pengguna yang tersimpan di cloud database secara realtime.
* **👤 Manajemen Profil & Pusat Bantuan**: Informasi akun pengguna dan panduan penggunaan aplikasi.

---

## 🛠️ Tech Stack & Arsitektur

* **Bahasa Pemrograman**: [Kotlin](https://kotlinlang.org/)
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) dengan [Material 3 Design System](https://m3.material.io/)
* **Arsitektur**: MVVM (Model-View-ViewModel) + StateFlow & Coroutines
* **Navigasi**: Navigation Compose (Single Activity Architecture)
* **Backend & Database**:
  * [Firebase Authentication](https://firebase.google.com/docs/auth) (Manajemen akun pengguna)
  * [Cloud Firestore](https://firebase.google.com/docs/firestore) (Database NoSQL realtime)
  * [Firebase Storage](https://firebase.google.com/docs/storage) (Penyimpanan gambar cloud)
* **Image Loading**: [Coil Compose](https://coil-kt.github.io/coil/compose/)

---

## 📂 Struktur Direktori

```text
Axl-Rent/
├── app/
│   ├── src/main/
│   │   ├── java/com/axlrent/
│   │   │   ├── data/            # Data models & sample data
│   │   │   ├── navigation/      # NavHost & rute navigasi aplikasi
│   │   │   ├── ui/
│   │   │   │   ├── screens/     # Layar Jetpack Compose (Home, Booking, Detail, dll)
│   │   │   │   └── theme/       # Desain tema, warna, dan tipografi
│   │   │   ├── viewmodel/       # State management & business logic
│   │   │   └── MainActivity.kt  # Entry point aplikasi
│   │   ├── res/                 # Resource gambar, ikon, dan strings
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── google-services.json.example  # Template kredensial Firebase
├── ui/                          # Dokumentasi screenshot aplikasi
├── wireframe/                   # Desain wireframe awal
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Cara Menjalankan Project

### 1. Prasyarat
* Android Studio (Ladybug / Koala atau versi terbaru yang mendukung Kotlin 1.9+)
* JDK 17
* Akun Firebase (untuk database & autentikasi)

### 2. Kloning Repository
```bash
git clone https://github.com/axcel-vf/axl-rent.git
cd axl-rent
```

### 3. Konfigurasi Firebase
1. Buat project baru di [Firebase Console](https://console.firebase.google.com/).
2. Daftarkan aplikasi Android dengan package name: `com.axlrent`.
3. Aktifkan **Firebase Authentication** (Email/Password) dan **Cloud Firestore**.
4. Download file `google-services.json` dari Firebase Console.
5. Letakkan file tersebut di dalam folder `app/` (sejajar dengan file `google-services.json.example`).

### 4. Build dan Jalankan Aplikasi
1. Buka project di **Android Studio**.
2. Tunggu proses **Gradle Sync** selesai.
3. Hubungkan perangkat Android fisik (aktifkan *USB Debugging*) atau jalankan *Android Emulator*.
4. Klik tombol **Run 'app'** (`Shift + F10`).

---

## 💡 Apa yang Dipelajari & Tantangan Teknis

* **Adopsi Jetpack Compose Penuh**: Membangun 100% antarmuka secara deklaratif menggunakan Material 3, mengelola State (`mutableStateOf`, `collectAsState`), dan transisi antar layar menggunakan Navigation Compose.
* **Integrasi Firebase Cloud**: Menghubungkan otentikasi pengguna secara aman dan menangani sinkronisasi data sewa secara realtime dengan Cloud Firestore.
* **Keamanan Kredensial**: Menerapkan praktik terbaik (*best practices*) dengan memisahkan file konfigurasi sensitif (`google-services.json`) dari repository publik menggunakan `.gitignore` dan menyediakan file `.example`.

---

## 👨‍💻 Pengembang

* **Nama**: Yhonatan Axcel Verdy Febrian
* **GitHub**: [@axcel-vf](https://github.com/axcel-vf)
* **Email**: yhonatanaksel3131@gmail.com
* **Project**: Pemrograman Mobile - Semester 6
