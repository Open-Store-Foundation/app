# OpenStore

An open-source, cross-platform application store built with Kotlin Multiplatform and Compose Multiplatform.

## Platforms

This application is built to run on the following platforms:

-   **Android**
-   **Desktop** (Windows, macOS, Linux)

Support for iOS is planned for the future.

## Tech Stack

-   **Kotlin Multiplatform**: For sharing code between platforms.
-   **Compose Multiplatform**: For building declarative UIs from a single codebase.
-   **Kotlin Coroutines**: For asynchronous programming.
-   **Ktor**: For networking.
-   **Room**: For local database storage.
-   **MVI (Model-View-Intent)**: Architectural pattern.
-   **Kitten**: Internal framework for Dependency Injection.

## Getting Started

### Prerequisites

-   Java Development Kit (JDK) 21 or higher.
-   Android Studio (for Android development).

### Building and Running

#### Android

To build and run the application on Android, you will need to configure debug signing. Create a file named `local.properties` in the root of the project with the following content:

```properties
foundation.openstore.key.debug.keystore=<path_to_your_debug.keystore>
foundation.openstore.key.debug.keystore_pass=<keystore_password>
foundation.openstore.key.debug.alias=<key_alias>
foundation.openstore.key.debug.alias_pass=<key_alias_password>
```

Once the `local.properties` file is configured, you can proceed with the following steps:

1.  Open the project in Android Studio.
2.  Let Gradle sync finish.
3.  Select the `app` run configuration.
4.  Choose a target device (emulator or physical).
5.  Click the "Run" button.

You can also build a debug APK from the command line:

```bash
./gradlew :app:assembleDebug
```

The output APK will be located in `app/build/outputs/apk/debug/`.

**Note**: Release builds are generated without a signature. They are intended to be signed later using a separate tool like `apktool`.

#### Desktop

To run the desktop application, you can use the following Gradle tasks from the root directory of the project:

```bash
# Run with hot reload
./gradlew :app:runHot

# Run a standard build
./gradlew :app:run
```

To build native distribution packages (DMG, MSI, Deb), use:

```bash
./gradlew :app:package
```

The outputs will be located in `app/build/compose/binaries/main/app`.

## Project Structure

The project is organized into several modules:

-   `app`: The main application module containing the shared UI and logic, along with platform-specific entry points for Android and Desktop.
-   `core`: A collection of modules that provide core functionalities:
    -   `async`: Asynchronous operation utilities.
    -   `common`: Common utilities and extensions.
    -   `config`: Configuration management.
    -   `log`: Logging infrastructure.
    -   `mvi`: MVI architecture components.
    -   `net`: Networking clients (Ktor, JSON-RPC).
    -   `os`: Operating system specific integrations.
    -   `store`: Data store and repository patterns.
    -   `strings`: Shared string resources.
-   `features`: Feature modules.
    -   `catalog`: The application catalog feature.
-   `installer`: Logic for installing applications.
-   `ui`: A library of shared, reusable UI components built with Compose Multiplatform.
-   `convention-plugins`: Custom Gradle plugins to simplify build configuration across modules.
