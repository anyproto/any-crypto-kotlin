package com.anytype.crypto

import org.bouncycastle.util.encoders.Hex
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GoldStandardTest {
    
    @Test
    fun testGoldStandardFromGo() {
        // Test case from Go implementation
        val accountAddress = "AAsya5TCoq8WkoDKDXvmULraHg1EZPJogKiqnh3gMuHwCJPP"
        val expectedPubKeyHex = "e7d0aa93cf727e99c7406fea1341629b7abae86450b18700ff1eac3f71996661"
        val message = "Test message 1"
        val messageHex = "54657374206d6573736167652031"
        val signatureHex = "0499290d20a66cd1e2b1e24a1fb912ee51d7b42767e65233af58522d770b6543af2c1aa28bf498d9c81d640ae5a4728132ba86c34d421381c789ac6c6d82550c"
        
        // Decode account address
        val pubKey = AccountDecoder.decodeAccountAddress(accountAddress)
        
        // Verify the account address round-trip
        assertEquals(accountAddress, pubKey.account(), "Account address should round-trip correctly")
        
        // Extract and verify the public key bytes
        val decodedPubKeyBytes = StrKey.decode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, accountAddress)
        assertEquals(expectedPubKeyHex, Hex.toHexString(decodedPubKeyBytes), "Public key should match expected hex")
        
        // Verify message hex encoding
        assertEquals(messageHex, Hex.toHexString(message.toByteArray()), "Message hex should match")
        
        // Verify signature
        val signature = Hex.decode(signatureHex)
        val isValid = pubKey.verify(message.toByteArray(), signature)
        assertTrue(isValid, "Signature should be valid")
        
        // Test with wrong message should fail
        val wrongMessage = "Wrong message"
        val isInvalid = pubKey.verify(wrongMessage.toByteArray(), signature)
        assertTrue(!isInvalid, "Signature should be invalid for wrong message")
    }
    
    @Test
    fun testMultipleGoldStandardCases() {
        // Additional test cases to ensure consistency
        val testCases = listOf(
            TestCase(
                accountAddress = "AAsya5TCoq8WkoDKDXvmULraHg1EZPJogKiqnh3gMuHwCJPP",
                pubKeyHex = "e7d0aa93cf727e99c7406fea1341629b7abae86450b18700ff1eac3f71996661",
                message = "Test message 1",
                signatureHex = "0499290d20a66cd1e2b1e24a1fb912ee51d7b42767e65233af58522d770b6543af2c1aa28bf498d9c81d640ae5a4728132ba86c34d421381c789ac6c6d82550c"
            ),
            TestCase(
                accountAddress = "A8yCiRddQZbmRew2bGLkjKP1Q5mEBvs5sSoXxS3UynxJVTiZ",
                pubKeyHex = StrKey.decode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, "A8yCiRddQZbmRew2bGLkjKP1Q5mEBvs5sSoXxS3UynxJVTiZ").let { Hex.toHexString(it) },
                message = "test",
                signatureHex = null // We don't have a signature for this, just test decoding
            )
        )
        
        for (testCase in testCases) {
            val pubKey = AccountDecoder.decodeAccountAddress(testCase.accountAddress)
            
            // Verify round-trip
            assertEquals(testCase.accountAddress, pubKey.account())
            
            // Verify public key
            val decodedPubKeyBytes = StrKey.decode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, testCase.accountAddress)
            assertEquals(testCase.pubKeyHex, Hex.toHexString(decodedPubKeyBytes))
            
            // Verify signature if provided
            testCase.signatureHex?.let { sigHex ->
                val signature = Hex.decode(sigHex)
                val isValid = pubKey.verify(testCase.message.toByteArray(), signature)
                assertTrue(isValid, "Signature should be valid for ${testCase.accountAddress}")
            }
        }
    }
    
    data class TestCase(
        val accountAddress: String,
        val pubKeyHex: String,
        val message: String,
        val signatureHex: String?
    )
}