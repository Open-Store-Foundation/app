# Firebox (Signer)

The **Firebox** is a secure key management and signing application for the OpenStore ecosystem.

## Platforms

-   **Android**: Supported.
-   **iOS**: Supported.
-   **Web**: In-progress (Kotlin/Wasm).
-   **Native** (Desktop/CLI): Planned for the near future.

### Building and Running

#### Android

To build and run the application on Android, you will need to configure debug signing. Create a file named `local.properties` in the root of the project with the following content:

```properties
foundation.openstore.signer.key.debug.keystore=<path_to_your_debug.keystore>
foundation.openstore.signer.key.debug.keystore_pass=<keystore_password>
foundation.openstore.signer.key.debug.alias=<key_alias>
foundation.openstore.signer.key.debug.alias_pass=<key_alias_password>
```

```properties
foundation.openstore.signer.key.release.keystore=<path_to_your_debug.keystore>
foundation.openstore.signer.key.release.keystore_pass=<keystore_password>
foundation.openstore.signer.key.release.alias=<key_alias>
foundation.openstore.signer.key.release.alias_pass=<key_alias_password>
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

## Project Structure

-   `app`: Main application and UI logic.
-   `core`: Core cryptographic and utility modules.
    -   `cryptography`: Cryptographic primitives and key management.
-   `data`: Data layer and persistence.
-   `SignerIos`: iOS-specific implementation.

## Getting Started

(Instructions for building and running the signer app will be added here.)
