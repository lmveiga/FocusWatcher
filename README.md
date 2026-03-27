# FocusWatcher

This is an Android application built for a technical interview to showcase architecture principles.

## App Structure

The codebase is a **single Gradle module** (`app`) with packages that map to Clean Architecture rings: **domain** at the center, **data** and **core** as infrastructure/application details, and **ui** + **service** at the Android boundary.

- **`domain`** — Domain models, repository definition and use cases . No Android APIs; persistence and network are abstracted behind repository contracts invoked by these use cases.
- **`data`** — Repository Implementation, Room for local storage, Retrofit API definition, a mock implementation and DTOs for the REST-shaped API.
- **`core`** — Dependency Injection, Notification Manager, Android Sensor abstraction and implementation, Logger Protocol and Session Manager, responsible for orchestrating active focus sessions between service and viewmodel.
- **`ui`** — Jetpack Compose screens, view state and events, model mapping for the UI, Material 3 theming
- **`service`** — `FocusSessionService`: foreground service that runs while monitoring is active; subscribes to sensor flows, applies thresholds and throttling, and notifies on noise or movement. Keeps heavy sensor lifecycle out of Composables and ViewModels.

The foreground service was chosen due to limited microphone access in recent Android versions in background services. Foreground service will allow for continuous monitoring while the app is active.

