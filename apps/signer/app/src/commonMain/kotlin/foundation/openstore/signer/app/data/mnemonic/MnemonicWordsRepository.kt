package foundation.openstore.signer.app.data.mnemonic

interface MnemonicWordsRepository {
    suspend fun getPrompts(text: String): List<String>
    suspend fun getWords(): List<String>
}

