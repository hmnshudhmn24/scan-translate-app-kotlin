# Scan & Translate App

A powerful Android application built with Kotlin and ML Kit that scans text from documents or handwriting and translates it into any supported language. It keeps a translation history and allows users to easily share scanned text.

## ✨ Features

- 📄 **Multilingual OCR** – extract printed or handwritten text using ML Kit.
- 🌍 **Instant Translation** – translate scanned text between dozens of languages.
- 🧭 **History Screen** – view past scans with source and target language.
- 🔗 **Share Translations** – send translated text via other apps.

## 📦 Tech Stack

- Kotlin  
- CameraX + ML Kit Text Recognition  
- ML Kit Translate  
- Room DB for history  
- Jetpack Compose UI  

## 🛠️ How to Run

1. **Clone the repo**:  
```bash
git clone https://github.com/yourusername/scan-translate-app-kotlin.git
cd scan-translate-app-kotlin
```

2. **Open in Android Studio** (Electric Eel or later).

3. **Add ML Kit API Keys**:  
   Ensure Firebase/ML Kit config is added via `google-services.json`.

4. **Build & Run**:  
   Connect an Android device and press ▶️ in Android Studio.

> Requires: Android 8+ device, camera permission, and internet for translation models.

## 🧩 Future Enhancements

- Add selection of target language via dropdown  
- Auto-download translation models for offline use