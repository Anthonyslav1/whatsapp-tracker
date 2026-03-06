# Ravdesk (formerly WhatsApp Tracker)

Ravdesk is an on-device, privacy-focused Android application that tracks your WhatsApp usage. It records which chat or status you are actively viewing and for how long, allowing you to generate comprehensive activity reports such as "Total Time Spent", "Your #1 Best Friend", "Longest Chat", and "The Entertainer" (whose statuses you watched the most).

**Disclaimer**: All data stays entirely on your local device. No chat content is ever logged, read, or transmitted. The app solely relies on Android's limited Accessibility Services to parse the names of chats/statuses and calculate time on screen.

## Features

- **Activity Dashboard**: View top contacts and daily usage patterns.
- **Yearly Wrapped Report**: A swipeable, shareable "Year in Review" breakdown with engaging metrics (Top 5, Most Active Month, Fun Facts).
- **Status Tracking ("The Entertainer")**: Tracks how long you spend viewing specific contacts' WhatsApp Statuses.
- **100% Offline & Private**: Backed by a local Room SQLite database. No internet permission required.
- **Resilient Background Service**: Utilizes robust `WorkManager` background enqueueing to ensure active sessions are reliably saved even if the app crashes or is aggressively killed by the OS.

## Technical Architecture

- **Kotlin** & **Coroutines** (incorporating `async` parallel DB aggregations).
- **Jetpack Compose** (Modular UI architecture with extracted card-based widgets).
- **Room Database** (SQLite persistence layer).
- **Hilt** (Dependency Injection) & **HiltWorker**.
- **Android AccessibilityService** (Debounced & deeply-scoped UI Node tree traversal via recursive BFS).
- **Unit Testing**: Complex mock tree testing via MockK.

## Setup & Building

You can build this project directly via Android Studio:

1. Clone the repository.
2. Open the project in Android Studio.
3. Sync Gradle and hit **Run** on a physical device. (Note: Accessibility parsing relies on the actual WhatsApp application UI, making emulator testing difficult).
4. **Enable Accessibility Service**: Once installed, navigate to your device's `Settings -> Accessibility -> Installed Apps -> Ravdesk`, and turn the service on.
