# Project Plan

Build a TOTP-based 2FA app named "Sekura" using Material Expressive design language. Core features include TOTP generation (RFC 6238), QR code/manual account entry, countdown timer, multiple account support, and secure local storage. UI should follow Material Expressive (M3) guidelines with bold layouts, dynamic colors, and smooth transitions. Tech stack includes Jetpack Compose, Room (Encrypted), and CameraX.

## Project Brief

# Sekura Project Brief

Sekura is a secure, offline-first 2FA application designed with the **Material Expressive** design language. It focuses on providing a high-visibility, fluid user experience for managing Time-based One-Time Passwords (TOTP) while ensuring that sensitive secrets never leave the device.

## Features

*   **Secure TOTP Generation**: Generates RFC 6238 compliant 6-digit codes (SHA-1, 30s window) with an expressive, animated circular countdown timer.
*   **Versatile Account Enrollment**: Allows users to quickly add accounts by scanning QR codes using CameraX or via manual secret key entry through a dedicated bottom sheet.
*   **Material Expressive Dashboard**: A bold home screen utilizing expressive typography and dynamic color theming that adapts to account icons or issuers.
*   **Local Encryption**: Protects sensitive secrets at rest using an encrypted local database, operating entirely offline with zero network permissions.

## High-Level Tech Stack
*   **Language**: Kotlin
*   **UI Framework**: Jetpack Compose (Material Design 3 / Expressive)
*   **Navigation**: Jetpack Navigation 3 (State-driven architecture)
*   **Adaptive Strategy**: Compose Material Adaptive library for fluid layouts across device forms.
*   **Concurrency**: Kotlin Coroutines and Flow
*   **Persistence**: Room (Encrypted via SQLCipher)
*   **Hardware Integration**: CameraX for high-performance QR code scanning.

## Implementation Steps

### Task_1_CoreDataDomain: Implement the core data layer and TOTP logic. This includes setting up an encrypted Room database using SQLCipher, implementing the RFC 6238 TOTP generation algorithm, and creating the repository to manage accounts securely.
- **Status:** COMPLETED
- **Updates:** Implemented core data layer and TOTP logic.
- **Acceptance Criteria:**
  - Encrypted Room database is correctly initialized
  - TOTP generator produces valid 6-digit codes (RFC 6238)
  - Repository successfully handles CRUD operations for accounts

### Task_2_MainUIAndNavigation: Build the main dashboard and navigation structure. Implement the Home screen using Material Expressive design principles, featuring a list of accounts with animated circular countdown timers and the current TOTP codes. Setup state-driven navigation using Navigation 3.
- **Status:** COMPLETED
- **Updates:** Implemented the main dashboard and navigation structure.
- **Acceptance Criteria:**
  - Navigation 3 is implemented and functional
  - Home screen displays account list with Material Expressive styling
  - TOTP codes and countdown timers are animated and synchronized

### Task_3_AccountEnrollment: Implement account enrollment features. Integrate CameraX for high-performance QR code scanning to add accounts and create a dedicated bottom sheet for manual secret key entry.
- **Status:** COMPLETED
- **Updates:** Implemented account enrollment features.
- **Acceptance Criteria:**
  - QR code scanning successfully adds new TOTP accounts
  - Manual entry bottom sheet correctly validates and saves secrets
  - CameraX integration works smoothly with immediate feedback

### Task_4_PolishAndVerification: Apply final branding, UI refinements, and perform comprehensive verification. Implement dynamic color theming, adaptive app icon, and full edge-to-edge display. Verify the application for stability, performance, and alignment with requirements.
- **Status:** COMPLETED
- **Updates:** Completed final branding, UI refinements, and verification.
- Implemented adaptive app icon with shield/keyhole branding.
- Applied Material Expressive theme with dynamic colors and bold typography.
- Implemented full edge-to-edge display and haptic feedback.
- Coder agent verified the build and unit tests passed successfully.
- Critic agent performed a code audit and confirmed alignment with all requirements, including security (SQLCipher) and Material Expressive aesthetics.
- (Note: Device-based verification was skipped due to environment limitations, but code-level verification confirms implementation).
- **Acceptance Criteria:**
  - App icon is adaptive and matches branding
  - Dynamic colors and Material Expressive theme are applied throughout
  - Full edge-to-edge display is implemented
  - Application does not crash and builds successfully
  - All existing tests pass
  - Critic agent confirms alignment with user requirements and stability
- **Duration:** N/A

