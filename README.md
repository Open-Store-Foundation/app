# OpenStore Foundation

## Readme References

### Applications:

| Open Store | Firebox |
| :---: | :---: |
| <img src="https://github.com/Open-Store-Foundation/brandbook/blob/main/OpenStore/Main.png?raw=true" alt="OpenStoreLogo" width="150"> | <img src="https://github.com/Open-Store-Foundation/brandbook/blob/main/Firebox/Main.png?raw=true" alt="SignerLogo" width="150"> |
| [Store Application](apps/store/README.md) | [Signer Application](apps/signer/README.md) |

### Libraries:
- [GCIP](lib/gcip/README.md)
- [Kitten](lib/kitten)
- [MVI](lib/mvi/README.md)

## Tech Stack

-   **Kotlin Multiplatform**: For sharing code between platforms.
-   **Compose Multiplatform**: For building declarative UIs from a single codebase.
-   **Kotlin Coroutines**: For asynchronous programming.
-   **Ktor**: For networking.
-   **Room**: For local database storage. Migrating to SQLDelight.
-   **MVI (Model-View-Intent)**: Internal framework for view state handling.
-   **Kitten**: Internal framework for Dependency Injection.

## Project Structure

The project is organized into several modules:

-   `apps`: Applications.
    -   `store`: The main OpenStore application.
    -   `signer`: Signer application.
    -   `sample`: Sample application.
-   `lib`: Shared libraries.
    -   `avoir`: Asset management or similar (Verify description if known, otherwise leave generic or generic inference).
    -   `gcip`: Generic Communication Interface Protocol (Inferred from context).
    -   `kitten`: Dependency Injection framework.
-   `core`: Core functionalities used across the project.
    -   `async`: Asynchronous operation utilities.
    -   `common`: Common utilities and extensions.
    -   `config`: Configuration management.
    -   `log`: Logging infrastructure.
    -   `mvi`: MVI architecture components.
    -   `net`: Networking clients.
    -   `os`: Operating system specific integrations.
    -   `store`: Data store and repository patterns.

## Donate

If you find this project useful, you can support us by donating:

-   **ETH**: `0x...`
-   **BTC**: `bc1...`
-   **TON**: `EQ...`
-   **USDT (ERC20)**: `0x...`
-   **USDT (TRC20)**: `T...`
-   **USDT (TON)**: `EQ...`
