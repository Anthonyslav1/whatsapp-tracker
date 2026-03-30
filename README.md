# Ravdesk (formerly WhatsApp Tracker)

Ravdesk is an on-device, privacy-focused Android application that tracks your WhatsApp usage. It records which chat or status you are actively viewing and for how long, allowing you to generate comprehensive activity reports such as "Total Time Spent", "Your #1 Best Friend" (Engagement Score), "Longest Chat", and "The Entertainer" (whose statuses you watched the most).

**Disclaimer**: All data stays entirely on your local device. No chat content is ever logged, read, or transmitted. The app solely relies on Android's limited Accessibility Services to parse the names of chats/statuses and calculate time on screen.

## Features

- **Activity Dashboard**: View top contacts and daily usage patterns.
- **Yearly Wrapped Report**: A swipeable, shareable "Year in Review" breakdown with engaging metrics (Top 5, Most Active Month, Fun Facts).
- **Relationship Score**: A proprietary engagement metric that goes beyond simple time spent, weighting both duration and frequency of interaction to identify your true closest contacts.
- **Smart Insights**: Automated detection of communication patterns (e.g., identifying if you are a "burst" or "balanced" communicator).
- **Viral Sharing ("Share Your Meta")**: Easily share your personalized communication statistics with friends while encouraging organic growth.
- **Status Tracking ("The Entertainer")**: Tracks how long you spend viewing specific contacts' WhatsApp Statuses.
- **100% Offline & Private**: Backed by a local Room SQLite database. No internet permission required.
- **Resilient Background Service**: Utilizes robust `WorkManager` background enqueueing to ensure active sessions are reliably saved even if the app crashes or is aggressively killed by the OS.

## 🚀 Changes

### 🎉 Cinematic UI Overhaul
We've completely redesigned Ravdesk to feel significantly more premium, sleek, and immersive!

* **Dark & Tactile Design:** Say goodbye to flat screens! The app now features a beautiful deep-dark background, vibrant neon cyan accents, and elegant new typography. We've also added subtle vibrations (haptics) across the app, so your buttons and scrolls actually *feel* responsive.
* **Animated Dashboard:** Your daily stats now smoothly glide onto the screen when you open the app. The old screen-time blocks have been upgraded to glowing circular "System Integrity" rings that track your interaction capacity in real-time.
* **The "Wrapped" Experience, Upgraded:** The Yearly Report is no longer a simple swipe-carousel. It’s now a continuous cinematic vertical feed! We’ve added visually stunning new data cards, including a grayscale "Best Friend" card and a GitHub-style "Activity Heatmap" so you can visually see precisely when your interactions peaked.

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
