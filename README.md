# PodoApp3 (Android)

Εφαρμογή διαχείρισης πελατών/ραντεβού/επισκέψεων για ποδολογικό ιατρείο, με Room DB και σύγχρονο Android UI.

## 🎯 Χαρακτηριστικά (σε εξέλιξη)
- Καρτέλα πελάτη & ιστορικό (αναμνηστικό) με tabs (Ιατρικά, Στάση/Παραμορφώσεις, Αριστερό/Δεξί πόδι, Οίδημα/Κιρσοί, Ορθωτικά).
- Ραντεβού & Επισκέψεις με ημερολόγιο και λίστες.
- Αναζήτηση/Φίλτρα/Sort (προς υλοποίηση για ραντεβού/επισκέψεις).

## 🧱 Τεχνολογίες
- Kotlin, AndroidX, Material Components
- Room (runtime/ktx) για local DB
- Coroutines + lifecycleScope
- ViewBinding, ViewPager2

## 🗂 Αρχιτεκτονική (high-level)
- `data/` : Room entities/dao/database
- `ui/`   : Activities/Fragments/Adapters
- `res/`  : Layouts, drawables, strings, themes

## 🧰 Απαιτήσεις
- Android Studio Giraffe/Koala+
- Android SDK 34
- JDK 17
- Gradle Wrapper (περιλαμβάνεται)

## ▶️ Build & Run
```bash
./gradlew clean build
# ή από Android Studio: Build > Make Project
