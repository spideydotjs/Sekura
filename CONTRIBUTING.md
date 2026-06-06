# Contributing to Sekura

Thank you for your interest in improving **Sekura**! Contributions from the community are what make open-source projects amazing. Please take a moment to review this document before submitting your contribution.

---

## 🐛 Reporting Bugs

If you find a bug:
1. Search the existing **Issues** to make sure it hasn’t already been reported.
2. If it is new, open a new Issue. Describe the bug, provide steps to reproduce, and specify your Android device model/OS version.
3. If applicable, attach logs or screenshots.

## 💡 Feature Requests

We welcome feature suggestions! To request a feature:
1. Open an Issue and select the feature request template (if available).
2. Clearly explain the feature, why it is useful, and how you envision it working.

## 🛠️ Code Contribution Workflow

If you want to contribute code:
1. **Fork** the repository and create your branch from `main`:
   ```bash
   git checkout -b feature/your-awesome-feature
   ```
2. Make your changes in your branch.
3. Ensure the project builds successfully and passes formatting rules:
   ```bash
   ./gradlew assembleDebug
   ```
4. Commit your changes with a descriptive commit message (e.g. `feat: add support for dynamic theme colors`).
5. Push to your fork and submit a **Pull Request** to the `main` branch.

## 🎨 Coding Guidelines

*   **Language**: Use Kotlin for all new code.
*   **Jetpack Compose**: Follow standard Jetpack Compose guidelines. Keep Composable functions clean, reusable, and extract UI state into ViewModels where appropriate.
*   **Database**: Ensure any changes to Room entities or DAOs are accompanied by appropriate migrations if needed.
*   **Formatting**: Follow the official [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html).
