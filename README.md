# 📖 QuranApp

**QuranApp** adalah aplikasi Android modern berbasis **Jetpack Compose** yang memungkinkan pengguna untuk:

- Membaca ayat-ayat Al-Qur'an dalam teks Arab (Uthmani) beserta terjemahan bahasa Indonesia.
- Mendengarkan audio murottal setiap ayat (oleh Mishary Rashid Alafasy).
- Menandai ayat-ayat favorit dengan fitur bookmark.
- Navigasi mudah berdasarkan **Surah** maupun **Juz**.

Aplikasi ini menggunakan API dari [AlQuran Cloud](https://alquran.cloud) untuk menyediakan data teks, terjemahan, dan audio murottal secara daring.

---

## ✨ Fitur Unggulan

- 📘 **Baca Al-Qur'an**  
  Tampilkan teks Al-Qur’an dalam huruf Arab Uthmani lengkap dengan terjemahan bahasa Indonesia.

- 🔊 **Audio Murottal**  
  Dengarkan murottal ayat per ayat, lengkap dengan kontrol **Play**, **Pause**, dan **Stop**.

- 📂 **Tampilan Surah & Juz**  
  Pilih untuk menelusuri Al-Qur’an berdasarkan daftar Surah atau Juz.

- ⭐ **Bookmark Ayat**  
  Tandai ayat favorit Anda agar mudah diakses kembali kapan saja.

- 🚀 **Navigasi Intuitif**  
  Desain antarmuka modern, ringan, dan mudah digunakan. Mendukung scroll otomatis ke ayat yang dipilih.

---

## 🔪 Teknologi yang Digunakan

| Teknologi         | Detail                                   |
|-------------------|------------------------------------------|
| Bahasa            | Kotlin                                   |
| UI Framework      | Jetpack Compose                          |
| HTTP Client       | Retrofit                                 |
| Playback Audio    | Android MediaPlayer                      |
| API               | [AlQuran Cloud API](https://api.alquran.cloud/v1/) |
| Build Tool        | Gradle                                   |
| Target SDK        | Android 15 (API 35)                      |
| Minimum SDK       | Android 11 (API 30)                      |
| Java Version      | Java 11                                  |

---

## ⚙️ Prasyarat

Pastikan Anda telah menyiapkan:

- **Android Studio** versi terbaru (disarankan versi Iguana atau lebih baru)
- **Kotlin Plugin** terbaru
- **JDK 11**
- **Koneksi Internet** (untuk akses teks dan audio dari API)
- Perangkat Android fisik atau emulator dengan minimal API 30

---

## 🚀 Cara Menjalankan Proyek

1. **Clone Repository**
   ```bash
   git clone https://github.com/username/quranapp.git
   cd quranapp
   ```

2. **Buka di Android Studio**
   - Pilih menu `File > Open`
   - Arahkan ke folder `quranapp`

3. **Sinkronisasi Proyek**
   - Klik tombol `Sync Project with Gradle Files`

4. **Jalankan Aplikasi**
   - Hubungkan perangkat Android atau gunakan emulator
   - Klik tombol **Run** di toolbar Android Studio

---

## 🧱 Panduan Penggunaan

1. **Buka Aplikasi**
   - Tampilan awal akan menampilkan daftar Surah atau Juz

2. **Navigasi dan Baca**
   - Pilih Surah atau Juz untuk melihat ayat-ayatnya
   - Setiap ayat ditampilkan dalam teks Arab dan terjemahan

3. **Putar Audio**
   - Klik ikon **Play** pada ayat untuk mulai mendengarkan
   - Gunakan tombol **Pause** dan **Stop** sesuai kebutuhan

4. **Bookmark Ayat**
   - Klik ikon **Bookmark** untuk menyimpan ayat favorit

5. **Navigasi**
   - Gunakan tombol **Back** di TopAppBar untuk kembali

---

## 🤝 Kontribusi

Kami membuka kontribusi dari siapa saja! Berikut cara berkontribusi:

1. **Fork Repository**
   - Klik tombol `Fork` di GitHub

2. **Buat Branch Baru**
   ```bash
   git checkout -b feature/nama-fitur
   ```

3. **Lakukan Perubahan**
   - Tambahkan fitur baru atau perbaiki bug

4. **Commit dan Push**
   ```bash
   git commit -m "Menambahkan fitur nama-fitur"
   git push origin feature/nama-fitur
   ```

5. **Buat Pull Request**
   - Ajukan PR ke repository utama
   - Sertakan deskripsi perubahan dengan jelas

---

## 👤 Kontak Developer

| Nama     | [Masukkan Nama Anda]                   |
|----------|----------------------------------------|
| Email    | [12350112981@students.uin-suska.ac.id ]                  |
| GitHub   | [@Rikenro](https://github.com/Rikenro) |

---

Terima kasih telah menggunakan **QuranApp**!  
Jika Anda memiliki pertanyaan, masukan, atau ingin berkontribusi—jangan ragu untuk menghubungi saya 😊

