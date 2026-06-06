# <img src=".github/logo.svg" width="64" height="64" align="center" alt="Sekura Logo"> Sekura

Your secure, offline-first 2FA companion. Built with Jetpack Compose, Room, and SQLCipher.

---

**Sekura** is a modern, lightweight, and secure two-factor authentication (2FA) client for Android. It prioritizes user privacy, data security, and convenience by keeping your OTP credentials completely encrypted under your own control, with options for encrypted cloud backups.

---

## Key Features

*   **Encrypted Database**: Your secrets are stored locally in an SQLite database encrypted with **SQLCipher** and managed through **Room**.
*   **Google Drive Backup**: Secure, encrypted backup and restore integration via Google Drive (using Google Sign-In), ensuring you never lose access to your accounts.
*   **QR Enrollment**: Instantly scan and add 2FA credentials using the **Google MLKit Barcode Scanning** and **Jetpack CameraX** APIs.
*   **Material 3 Design**: A clean, modern user interface featuring smooth transitions, dynamic theme colors, and a clean, responsive layout.
*   **Offline-First**: Generating codes requires absolutely zero internet access. All data resides securely on your device.

---

## Tech Stack & Architecture

- **UI**: Jetpack Compose, Material 3, Compose Navigation
- **Local DB**: Room Database, SQLCipher (encryptions)
- **APIs**: MLKit Barcode Scanning, CameraX, Play Services Auth (Google Sign-In), Google APIs Client (Drive V3)
- **Concurrency**: Kotlin Coroutines, Flow
- **Dependency Processing**: KSP (Kotlin Symbol Processing)
- **Build System**: Gradle 9.4+ (Kotlin DSL), Android Gradle Plugin 9.2+

---

## Getting Started

### Prerequisites
*   Android Studio (Ladybug or newer recommended)
*   JDK 17 or 21
*   Android SDK 34+ (Target SDK: 37)

### Local Build
To compile and build a debug APK locally, clone the repository and run:
```bash
./gradlew assembleDebug
```

---

## CI/CD Release Pipeline

This project includes a fully automated GitHub Actions workflow to build, sign, and publish your release APKs directly to GitHub Releases.

The workflow is configured in [.github/workflows/build-apk.yml](file:///home/sonu/AndroidStudioProjects/Sekura/.github/workflows/build-apk.yml).

### Configured Secrets
To use the build pipeline, configure the following secrets in your GitHub repository (**Settings ➔ Secrets and variables ➔ Actions**):

*   `GOOGLE_SERVICES_JSON`: The raw contents of your `google-services.json` file.
*   `KEYSTORE_BASE64`: A Base64-encoded string of your release `.keystore` or `.jks` file.
*   `KEYSTORE_PASSWORD`: The password for the keystore.
*   `KEY_ALIAS`: The key alias name.
*   `KEY_PASSWORD`: The password for the key.

For step-by-step instructions on generating the secrets, see the [Build & Release Setup Guide](file:///home/sonu/.gemini/antigravity-cli/brain/0a3b0a94-e275-4d7e-a6f9-f5c58bf334ac/github_release_setup.md).

---

## License & Contribution

This project is licensed under the [MIT License](LICENSE). See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to report bugs, suggest features, and submit pull requests.
