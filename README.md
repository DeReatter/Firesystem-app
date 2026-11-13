# Firesystem-app

Aplikasi Android untuk memantau kondisi sensor dan mengendalikan sistem proteksi kebakaran (Fire System) berbasis IoT secara jarak jauh.

## Ekosistem Project

Aplikasi ini terhubung dengan perangkat keras pada repository berikut:
* **Aplikasi Android (Repo ini):** Monitoring status sensor, notifikasi peringatan, dan kontrol perangkat.
* **Firmware STM32:** Pengolahan data sensor dan kontrol aktuator utama di [IOTProject-STM32](https://github.com/DeReatter/IOTProject-STM32).
* **Firmware ESP32:** Gateway komunikasi jaringan antara STM32 dan aplikasi (`[Tambahkan link repo ESP32 di sini]`).

## Fitur Utama

* Menampilkan data pembacaan sensor dari perangkat IoT secara real-time.
* Memberikan peringatan dini ketika terdeteksi potensi bahaya kebakaran.
* Kontrol manual untuk menyalakan atau mematikan aktuator maupun alarm langsung dari aplikasi.

## Struktur Project

* `app/src/main/java/` - Logika utama aplikasi, antarmuka pengguna, dan komunikasi jaringan.
* `app/src/main/res/` - Layout XML, ikon, dan aset tampilan.
* `build.gradle` - Konfigurasi dependencies dan versi SDK Android.

## Kebutuhan Sistem

* Android Studio (versi terbaru disarankan).
* Perangkat Android atau emulator dengan minimal `[sesuaikan minSdk, misal Android 8.0 / API 26]`.

## Cara Menjalankan Project

1. Clone repository ini:
   ```bash
   git clone [https://github.com/DeReatter/Firesystem-app.git](https://github.com/DeReatter/Firesystem-app.git)
2. Buka folder project menggunakan Android Studio dan tunggu proses Gradle sync selesai.
3. Sesuaikan konfigurasi koneksi server ([misal URL Firebase, MQTT broker, atau endpoint API]) di file [nama file konfigurasi].
4. Hubungkan perangkat Android dengan USB Debugging aktif atau jalankan emulator, lalu tekan Run.
