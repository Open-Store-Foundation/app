# OpenStore

An open-source, cross-platform application store built with Kotlin Multiplatform and Compose Multiplatform.

## Platforms

This application is built to run on the following platforms:

-   **Android**
-   **Desktop** (Windows, macOS, Linux)

Support for iOS is planned for the future.

## Getting Started

### Prerequisites

-   Java Development Kit (JDK) 21 or higher.
-   Android Studio (for Android development).

### Building and Running

#### Android

To build and run the application on Android, you will need to configure debug signing. Create a file named `local.properties` in the root of the project with the following content:

```properties
foundation.openstore.store.key.debug.keystore=<path_to_your_debug.keystore>
foundation.openstore.store.key.debug.keystore_pass=<keystore_password>
foundation.openstore.store.key.debug.alias=<key_alias>
foundation.openstore.store.key.debug.alias_pass=<key_alias_password>
```

```properties
foundation.openstore.store.key.release.keystore=<path_to_your_debug.keystore>
foundation.openstore.store.key.release.keystore_pass=<keystore_password>
foundation.openstore.store.key.release.alias=<key_alias>
foundation.openstore.store.key.release.alias_pass=<key_alias_password>
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

-   `app`: Main application module (Android, Deskop).
-   `core`: Core functionalities.
    -   `cert`: Certificate handling.
    -   `installer`: Application installer logic.
    -   `strings`: Shared string resources.
-   `feature`: Feature modules.
    -   `catalog`: Application catalog and listing.
