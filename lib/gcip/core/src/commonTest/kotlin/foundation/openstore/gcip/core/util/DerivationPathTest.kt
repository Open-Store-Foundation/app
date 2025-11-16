package foundation.openstore.gcip.core.util

import foundation.openstore.gcip.core.Blockchain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DerivationPathTest {

    @Test
    fun testDerivationBlockchains() {
        Blockchain.entries.forEach {
            assertTrue("Derivation path for ${it.name}-${it.curve.name}-${it.derivationPath} is invalid") {
                DerivationPath.validate(it.derivationPath, it.curve.isRequireHardened)
            }
        }
    }

    @Test
    fun testParsePath() {
        val path = "m/44'/60'/0'/0/0"
        val indices = DerivationPath.parsePath(path)
        
        assertEquals(5, indices.size)
        // 44' -> 44 | 0x80000000 = 2147483692
        assertEquals(2147483692u, indices[0])
        // 60' -> 60 | 0x80000000 = 2147483708
        assertEquals(2147483708u, indices[1])
        // 0' -> 0 | 0x80000000 = 2147483648
        assertEquals(2147483648u, indices[2])
        // 0 -> 0
        assertEquals(0u, indices[3])
        // 0 -> 0
        assertEquals(0u, indices[4])
    }
    
    @Test
    fun testIsHardened() {
        val hardened = DerivationPath.toHardened(0u)
        assertTrue(DerivationPath.isHardened(hardened))
        assertEquals(2147483648u, hardened)
    }

    @Test
    fun testUIntIndex() {
        val index = 3000000000u // Larger than Int.MAX_VALUE
        
        val hardened = DerivationPath.toHardened(index)
        // 3000000000 | 2147483648 = 3000000000 (0xB2D05E00 has high bit set)
        
        assertTrue(DerivationPath.isHardened(hardened))
        assertEquals(3000000000u, hardened)
    }
}
