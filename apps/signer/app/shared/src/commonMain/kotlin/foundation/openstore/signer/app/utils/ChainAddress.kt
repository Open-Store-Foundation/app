package foundation.openstore.signer.app.utils

import com.openstore.app.core.common.toLower0xHex
import com.openstore.core.signing.Base58
import com.openstore.core.signing.Bech32
import com.openstore.core.signing.Cryptography
import foundation.openstore.gcip.core.Blockchain

object ChainAddress {

    fun getAddress(publicKey: ByteArray, blockchain: Blockchain): String? {
        return when (blockchain) {
            Blockchain.Bitcoin -> bitcoinAddress(publicKey)
            Blockchain.Ethereum -> ethereumAddress(publicKey)
            Blockchain.Solana -> solanaAddress(publicKey)
            Blockchain.TRON -> tronAddress(publicKey)
            Blockchain.Cardano -> cardanoAddress(publicKey)
            Blockchain.Sui -> suiAddress(publicKey)
            Blockchain.TON -> null
        }
    }

    fun ethereumAddress(uncompressed65: ByteArray): String {
        require(uncompressed65.size == 65 && uncompressed65[0] == 0x04.toByte())
        // Drop 0x04
        val hash = Cryptography.Hash.keccak256(uncompressed65.copyOfRange(1, 65))
        return hash.copyOfRange(12, 32).toLower0xHex()
    }

    private fun solanaAddress(publicKey: ByteArray): String {
        // Base58 of public key (Ed25519, 32 bytes)
        require(publicKey.size == 32)
        return Base58.encode(publicKey)
    }

    private fun bitcoinAddress(publicKey: ByteArray): String {
        // P2WPKH (Native SegWit) - Bech32 "bc1q..."
        val compressed = Cryptography.Secp256k1.compressPubKey(publicKey)
        val sha256 = Cryptography.Hash.sha256(compressed)
        val ripemd160 = Cryptography.Hash.ripemd160(sha256)
        return Bech32.encodeSegwit("bc", 0, ripemd160)
    }

    private fun cardanoAddress(publicKey: ByteArray): String {
        // CIP-1852 Shelley Address
        // Enterprise Address (no staking): Header (0110 0001 = 0x61) or similar.
        // Actually:
        // Type 6: Enterprise address (payment part only).
        // Network Tag: 1 (Mainnet).
        // Header: 0110 | 0001 = 0x61 (Payment Key Hash, No Stake).
        // BUT commonly users want Base Address (Payment + Stake). We only have one key here.
        // If we derive stake key from same seed, we need the seed. Here we only have pubkey.
        // So we generate Enterprise Address.
        // Header = 0x60 (Mainnet, Payment Key Hash only) ?
        // Reference: CIP-0019
        // Type 6 (0110) - Enterprise address.
        // Network Tag: 0 (Testnet), 1 (Mainnet).
        // Mainnet header: 0110 0001 = 0x61.
        // Testnet header: 0110 0000 = 0x60.

        val header = 0x61.toByte()
        val blake2b = Cryptography.Hash.blake2b224(publicKey)
        val data = byteArrayOf(header) + blake2b

        return Bech32.encode("addr", data)
    }

    private fun tronAddress(publicKeyUncompressed: ByteArray): String {
        require(publicKeyUncompressed.size == 65 && publicKeyUncompressed[0] == 0x04.toByte())
        // Drop 0x04
        val hash = Cryptography.Hash.keccak256(publicKeyUncompressed.copyOfRange(1, 65))
        val addressBytes = byteArrayOf(0x41.toByte()) + hash.copyOfRange(12, 32)
        return Base58.encodeCheck(addressBytes)
    }

    private fun suiAddress(publicKey: ByteArray): String {
        // Hex(Blake2b256(0x00 (Ed25519) + PubKey))
        require(publicKey.size == 32)
        val data = byteArrayOf(0x00) + publicKey
        return Cryptography.Hash.blake2b256(data).toLower0xHex()
    }
}