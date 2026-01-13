package foundation.openstore.gcip.core

import foundation.openstore.gcip.core.transport.CoseAlgorithm
import foundation.openstore.gcip.core.transport.CoseCurve
import foundation.openstore.gcip.core.util.DerivationPath
import foundation.openstore.gcip.core.util.fromUrlBase64
import foundation.openstore.gcip.core.util.toUrlBase64Fmt

enum class Blockchain(
    val coinName: String,
    val derivationPath: String, // "m/44'/60'/0'/0/0"
    val curve: Algorithm
) {
    // TODO Add more
    Bitcoin("Bitcoin", "m/84'/0'/0'/0/0", Algorithm.Es256Secp256k1),
    Ethereum("Ethereum", "m/44'/60'/0'/0/0", Algorithm.Es256Secp256k1),
    Solana("Solana", "m/44'/501'/0'/0'", Algorithm.EddsaEd25519),
    TON("TON", "m/44'/607'/0'/0'/0'", Algorithm.EddsaEd25519),
    Cardano("Cardano", "m/1852'/1815'/0'/0'/0'", Algorithm.EddsaEd25519),
    TRON("TRON", "m/44'/195'/0'/0/0", Algorithm.Es256Secp256k1),
    Sui("SUI", "m/44'/784'/0'/0'/0'", Algorithm.EddsaEd25519),
    ;

    fun toDerivationType(): Derivation {
        return Derivation.Path(curve, derivationPath)
    }

    companion object {
        val BasicChains = listOf(
            Bitcoin,
            Ethereum,
            Solana,
        )

        // TODO search by part of path
        fun findByDerivationPath(path: String): Blockchain? {
            return entries.firstOrNull { path.startsWith(it.derivationPath) }
        }
    }
}

enum class Algorithm(
    val alg: CoseAlgorithm.Sig,
    val curve: CoseCurve.Sig,
    val displayName: String,
    val curveKey: String,
    val isRequireHardened: Boolean,
) {
    Es256Secp256k1(
        alg = CoseAlgorithm.Sig.Es256K,
        curve = CoseCurve.Sig.Secp256k1,
        displayName = "ECDSA Secp256k1",
        curveKey = "Bitcoin seed",
        isRequireHardened = false,
    ),
    Es256Secp256r1(
        alg = CoseAlgorithm.Sig.Es256,
        curve = CoseCurve.Sig.Secp256r1,
        displayName = "ECDSA Secp256r1",
        curveKey = "Nist256p1 seed",
        isRequireHardened = false,
    ),
    EddsaEd25519(
        alg = CoseAlgorithm.Sig.EdDsa,
        curve = CoseCurve.Sig.Ed25519,
        displayName = "EdDSA Ed25519",
        curveKey = "ed25519 seed",
        isRequireHardened = true,
    )
    ;

    val value: Int get() = alg.value

    companion object {
        fun from(value: Int): Algorithm? {
            return entries.firstOrNull { it.value == value }
        }
    }
}

sealed interface Derivation {

    val displayKey: String
    val uniqueKey: String

    val algo: Algorithm
    val derivation: String?

    class Algo(override val algo: Algorithm) : Derivation {
        override val displayKey: String get() = algo.value.toString()
        override val uniqueKey: String get() = displayKey
        override val derivation: String? get() = null
    }

    class Blob(override val algo: Algorithm, val value: ByteArray) : Derivation {
        val strData by lazy(LazyThreadSafetyMode.PUBLICATION) { value.toUrlBase64Fmt() }

        override val displayKey: String get() = strData
        override val uniqueKey: String get() = "blob:$displayKey"
        override val derivation: String get() = strData
    }

    class Path(override val algo: Algorithm, val path: String) : Derivation {

        init {
            require(DerivationPath.validate(path, algo.isRequireHardened)) { "Invalid derivation path: $path" }
        }

        override val displayKey: String get() = path
        override val derivation: String get() = path
        override val uniqueKey: String get() = displayKey
    }

    companion object Companion {
        fun from(algo: Algorithm, derivation: String?): Derivation {
            return when {
                derivation == null -> Algo(algo)
                derivation.startsWith("m") -> Path(algo, derivation)
                else -> Blob(algo, derivation.fromUrlBase64())
            }
        }
    }
}