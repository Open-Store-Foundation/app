package foundation.openstore.signer.app.data.mnemonic

import com.openstore.app.core.common.use
import foundation.openstore.signer.app.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
class CommonMnemonicWordsRepository : MnemonicWordsRepository {

    private var cachedWords: List<String>? = null

    private suspend fun ensureWords(): List<String> {
        cachedWords?.let { return it }
        
        val content = Res.readBytes("files/mnemonic_words.txt").decodeToString()
        val list = content.split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        cachedWords = list
        return list
    }

    override suspend fun getPrompts(text: String): List<String> {
        return ensureWords()
            .filter { it.startsWith(text, ignoreCase = true) }
            .take(20)
    }

    override suspend fun getWords(): List<String> {
        return ensureWords()
    }
}
