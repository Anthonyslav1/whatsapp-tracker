# WhatsApp Tracker - Architecture Overview

This document outlines the architectural patterns and technologies used in the WhatsApp Tracker application. The project strictly adheres to **Modern Android Development (MAD)** standards to ensure scalability, maintainability, and testability.

## Technology Stack

* **UI Framework**: Jetpack Compose
* **Architecture Pattern**: Model-View-ViewModel (MVVM)
* **Dependency Injection**: Dagger Hilt
* **Local Storage**: Room Database
* **Asynchronous Programming**: Kotlin Coroutines & Flows
* **Background Processing**: WorkManager
* **Navigation**: Type-Safe Navigation Compose (Kotlinx Serialization)

## High-Level Architecture

The application follows a strict separation of concerns, divided into three main layers:

### 1. UI Layer (Presentation)
* **Jetpack Compose**: The entire UI is built declaratively. Large screens (like `DashboardScreen`) are decomposed into small, reusable, and stateless components (e.g., `TodayTimeCard`, `TopContactsList`, `WrappedBanner`).
* **ViewModels**: Handle the presentation logic and manage UI state. They observe data from the Data layer using Kotlin `StateFlow` and expose it to the Compose UI. ViewModels do not contain any Android framework dependencies.
* **Viral Sharing**: Implemented via Android's native sharing Intent, allowing users to export a text-based summary of their yearly "Meta" data.
* **Type-Safe Navigation**: Navigation is handled using typed data objects (`Routes.Setup`, `Routes.Dashboard`, `Routes.YearlyReport`) rather than brittle string routes, preventing runtime crashes and ensuring arguments are type-checked at compile time.

### 2. Domain / Data Layer (Repositories)
* **Repository Pattern**: Acts as the single source of truth for all data operations. ViewModels interact only with Repository interfaces (e.g., `UsageRepository`).
* **Dependency Inversion**: By injecting interfaces rather than concrete implementations (e.g., binding `UsageRepositoryImpl` to `UsageRepository`), the application is highly modular and unit-testable using fakes or mocks.

### 3. Data Source Layer (Local DB & Services)
* **Room Database**: Handles local SQLite storage. All Room instances are scoped as singletons and managed entirely by Hilt (`DatabaseModule`). Manual singletons inside the classes are strictly omitted.
* **Data Transfer Objects (DTOs)**: Database models and query return types are strictly separated into dedicated files (e.g., `Models.kt`, `YearlyReportData.kt`) to keep the DAOs clean and focused purely on queries.
* **Engagement Logic**:
    * **Relationship Score**: Calculated directly in SQLite via the DAO, weighting session duration and frequency (total duration + count * weight).
    * **Smart Insights**: Generated in the Repository layer by analyzing usage distributions and categorizing users based on their "burstiness" or "regularity".
* **Accessibility Service**: The core tracking engine (`TrackerService`) operates as an Android AccessibilityService, capturing WhatsApp UI events. It relies on a specialized `WhatsAppAccessibilityParser` to intelligently extract meaningful chat context while aggressively filtering out noise (like generic UI strings "Search", "Settings", or instructions).

## Dependency Injection (Hilt)

Hilt manages the lifecycle and provisioning of all core dependencies, entirely replacing error-prone manual singleton patterns.
* `@Singleton`: Used for the Room `AppDatabase` and other cross-cutting dependencies.
* `@Binds`: Used idiomatically in abstract modules (like `RepositoryModule`) to bind implementation classes to their respective domain interfaces.
* `@HiltViewModel`: Simplifies the injection of Repositories into ViewModels.

## Background Execution

Heavy background lifting or periodic maintenance tasks (if any) use `WorkManager`. The application utilizes on-demand initialization for WorkManager (by excluding the default initializer from the Android manifest) to optimize startup performance and adheres to strict Android battery/lifecycle constraints. 

## Testing

The architecture is explicitly designed for test-driven development:
* **UI**: Compose allows for isolated, fast UI tests.
* **ViewModels**: By passing Repositories via constructor injection, we easily substitute `UsageRepository` with mock implementations using MockK for JVM unit tests.
* **Services**: The `WhatsAppAccessibilityParser` and similar logic can be tested using Robolectric to simulate `AccessibilityNodeInfo` trees without requiring a physical emulator or device.
