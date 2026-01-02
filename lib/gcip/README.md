# Generic Credential Interaction Protocol (GCIP)

A protocol based on FIDO2 WebAuthn principles for delegating private key storage and transaction signing to a separate secure application (Signer App).

### Specification
[CAIP-401](CAIP.md)

## Roadmap

### Tier 1 Priority - Stabilize API
1. Android Intent
2. iOS Action Extension
3. Web/Extension -> Android over QR+BLE
4. Web/Extension -> iOS over QR+BLE
5. Deeplinks Android
6. Deeplinks iOS
7. iOS -> Android over QR+BLE
8. Android -> iOS over QR+BLE


### Features Roadmap

Legend:
- ✅ : Done
- ⏳ : In-queue / Implementing
- ❌ : Not Implemented
- ⚫️ : Unsupported

| Feature                      | Android | iOS | JS | Desktop | Native |
|:-----------------------------|:-------:|:---:|:--:|:-------:|:------:|
| **Supported**                    |    ✅    |  ✅  | ⏳  |    ❌    |   ❌    |
| **Handshake**                |         |     |    |         |        |
| ECDH-P256                    |    ✅    |  ✅  | ✅  |    ✅    |   ✅    |
| EdDH-X25519                  |    ⏳    |  ⏳  | ⏳  |    ⏳    |   ⏳    |
| **Encryption**               |         |     |    |         |        |
| AES256                       |    ✅    |  ✅  | ✅  |    ✅    |   ✅    |
| **Connection**               |         |     |    |         |        |
| Native                       |    ✅    |  ✅  | ❌  |    ❌    |   ❌    |
| Deeplink                     |    ❌    |  ❌  | ❌  |    ❌    |   ❌    |
| BLE                          |    ❌    |  ❌  | ❌  |    ❌    |   ❌    |
| NFC                          |    ❌    |  ❌  | ❌  |    ❌    |   ❌    |
| USB                          |    ❌    |  ❌  | ❌  |    ❌    |   ❌    |
| QR                           |    ❌    |  ❌  | ❌  |    ❌    |   ❌    |
| **Apps**                     |         |     |    |         |        |
| Wallet Sample                |    ✅    |  ✅  | ✅  |    ❌    |   ❌    |
| Signer Sample                |    ✅    |  ✅  | ⚫  |    ❌    |   ⚫    |
| **Platform Features**        |         |     |    |         |        |
| Signer Caller's Parser       |    ✅    |  ⚫  | ❌  |    ❌    |   ⚫    |
| Signer Client's Verification |    ✅    |  ⚫  | ❌  |    ❌    |   ⚫    |
| Device Security Verification |    ✅    |  ❌  | ❌  |    ❌    |   ⚫    |
| Challenge Transform          |    ✅    |  ✅  | ✅  |    ✅    |   ✅    |
| **Credential SDK**           |         |     |    |         |        |
| ECDSA-P256 (Message)         |    ⏳    |  ⏳  | ⏳  |    ⏳    |   ⏳    |
| ECDSA-P256 (Digest)          |    ⏳    |  ⏳  | ❌  |    ⏳    |   ⏳    |
| ECDSA-Secp256k1 (Message)    |    ✅    |  ✅  | ⏳  |    ✅    |   ✅    |
| ECDSA-Secp256k1 (Digest)     |    ⏳    |  ⏳  | ❌  |    ⏳    |   ⏳    |
| EdDSA-Ed25519 (Message)      |    ✅    |  ✅  | ✅  |    ✅    |   ✅    |
| EdDSA-Ed25519 (Digest)       |    ⏳    |  ⏳  | ❌  |    ⏳    |   ⏳    |
| **Lib**                      |         |     |    |         |        |
| Wallet Core                  |    ✅    |  ✅  | ✅  |    ✅    |   ✅    |
| Signer Core                  |    ✅    |  ✅  | ✅  |    ✅    |   ✅    |
| Wallet Platform              |    ✅    |  ✅  | ⏳  |    ❌    |   ❌    |
| Signer Platform              |    ✅    |  ✅  | ✅  |    ❌    |   ❌    |
| Wallet SDK                   |    ⏳    |  ❌  | ❌  |    ❌    |   ❌    |
| Signer SDK                   |    ❌    |  ❌  | ⚫  |    ❌    |   ❌    |


### Common Roadmap

| Feature        | Implemented |
|:---------------|:-----------:|
| **Tests**      |      ❌      |
| **Publishing** |      ❌       |

## Dependencies

### GCIP DEPS

#### Security (will be replaced with internal tool)
- [rootbeer](https://github.com/scottyab/rootbeer) (root checker android)

#### Multiplatform BigInt
- [kotlin-multiplatform-bignum](https://github.com/ionspin/kotlin-multiplatform-bignum)
  - transition to -> [cryptography-kotlin](https://github.com/whyoleg/cryptography-kotlin)

#### EdDSA
- [curve25519-kotlin](https://github.com/andreypfau/curve25519-kotlin) (EDDSA)
  - transition to -> [cryptography-kotlin](https://github.com/whyoleg/cryptography-kotlin)
  - [PR #109 (xdh, eddsa)](https://github.com/whyoleg/cryptography-kotlin/pull/109)

#### ECDSA-secp256k1
- [secp256k1-kmp](https://github.com/ACINQ/secp256k1-kmp)
  - transition to -> [cryptography-kotlin](https://github.com/whyoleg/cryptography-kotlin)
  - [branch dev/getPublicKey](https://github.com/whyoleg/cryptography-kotlin/tree/dev/getPublicKey) (secp256k1, p256)

#### Transform Hashes
- [cryptohash](https://github.com/appmattus/crypto/tree/main/cryptohash)
  - transition to -> [cryptography-kotlin](https://github.com/whyoleg/cryptography-kotlin)

#### General Cryptography
- [cryptography-kotlin](https://github.com/whyoleg/cryptography-kotlin) (DH, AES, ECDSA, Transform Hashes)
  - [PR #136](https://github.com/whyoleg/cryptography-kotlin/pull/136) Block implementation for Credential SDK (all digest curves)
