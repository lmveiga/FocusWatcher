# FocusWatcher

This is an Android application built for a technical interview to showcase architecture principles.

## App Structure

The codebase is a **single Gradle module** (`app`) with packages that map to Clean Architecture rings: **domain** at the center, **data** and **core** as infrastructure/application details, and **ui** + **service** at the Android boundary.

- **`domain`** — Domain models, repository definition and use cases . No Android APIs; persistence and network are abstracted behind repository contracts invoked by these use cases.
- **`data`** — Repository Implementation, Room for local storage, Retrofit API definition, a mock implementation and DTOs for the REST-shaped API. The repository follows a single source of truth pattern, with the UI directly observing Room database for changes. Network sync occurs as a background side effect.
- **`core`** — Dependency Injection, Notification Manager, Android Sensor abstraction and implementation, Logger Protocol and Session Manager, responsible for orchestrating active focus sessions between service and viewmodel.
- **`ui`** — Jetpack Compose screens, view state and events, model mapping for the UI, Material 3 theming
- **`service`** — `FocusSessionService`: foreground service that runs while monitoring is active; subscribes to sensor flows, applies thresholds and throttling, and notifies on noise or movement. Keeps heavy sensor lifecycle out of Composables and ViewModels.

The foreground service was chosen due to limited microphone access in recent Android versions in background services. Foreground service will allow for continuous monitoring while the app is active.

This structure aims to keep Android logic as far away as possible from domain logic, making it easier to test. 

Some improvements that were deprioritized:
- Permissions could be better abstracted, making it easier to make adjustments if the app needs more permissions in the future.
- FocusNotificationManager could be more decoupled. Robolectric makes it possible to unit test it by mimicking Android behavior, but decoupling would allow faster and more consistent tests. For this MVP I considered this approach acceptable. 
- CoroutineScopes are being created and controlled directly in the DI. This is not an ideal solution. A more robust solution would define a provider class with relevant coroutineScopes.
- The mocked RESTful API is way too simple. In a real app, the models would need to contain user identification, handle authorization tokens and more.
- Code coverage is not perfect. Most relevant parts have tests included. 
- UseCases have no tests. Since they were basically bridging communication with the repository, their tests were deprioritized. In a production ready application those UseCases would probably contain way more logic and be thoroughly tested.
- Composables have no tests. One of the advantages of Jetpack Compose is easier UI testing, which is not being leveraged in the project currently. This was deprioritized as the UI is pretty simple and there were more important code to cover.
- Error Handling needs to be improved. Most of the error handling is done by silently failing. Some Android Hardware scenarios aren't dealing with potential crashes. The app does not check whether the sensors are available.
- FocusSessionScreenState is defined as a sealed class, as the application would evolve to having more complex Error Scenarios. Those would be represented by another children of the sealed class. Currently, it only holds a success state though.
- DI should more modularized. Currently, there are only a AppModule and DatabaseModule defined.
- Thresholds and throttleFirst logic are in the ForegroundService. They could be decoupled for better testing and maintainability.
- The retrieve and sync sessions usecases run on the init block of the ViewModel. In a production app, we'd delegate this to a WorkManager job. This ensures that the app is syncing even when close and can be bound to constraints, such as having a wifi connection.
- isSynced property on the SessionEntity isn't used currently. In a more robust implementation, we'd use this to flag sessions that don't need to be updated.

## Native Resource Handling
Regarding the sensor implementation (`core/sensor`), there are two interfaces created to bridge the native handling with our domain logic. By keeping the Android logic away from our domain logic, we can easily test our behavior without the complexity of the OS logic. 
The monitors are lifecycle bound to the service lifecycle, meaning they get released as soon as the session ends, avoiding unnecessary battery usage. On noise monitor we use a less-readable array manipulation to improve performance at the monitoring job, as the loop will be executed many times. 

## Testability
The app was developed with testability in mind. Every class in the app attempts to have a single responsibility, which allows for concise and predictable unit tests. We use Robolectric to mock the behavior of some Android classes. We use the StandardTestDispatcher to control the time in kotlin coroutines, building robust tests.  