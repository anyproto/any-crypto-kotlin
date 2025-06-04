package com.anytype.crypto

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AccountDecoderTest {
    
    @Test
    fun testDecodeAccountAddress() {
        val originalPubKey = ByteArray(32) { (it + 1).toByte() }
        val encodedAddress = StrKey.encode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, originalPubKey)
        
        val decodedPubKey = AccountDecoder.decodeAccountAddress(encodedAddress)
        
        assertEquals(encodedAddress, decodedPubKey.account())
        
        val testData = "test data".toByteArray()
        val testSig = ByteArray(64)
        decodedPubKey.verify(testData, testSig)
    }
    
    @Test
    fun testDecodeInvalidAddressThrows() {
        assertThrows<IllegalArgumentException> {
            AccountDecoder.decodeAccountAddress("invalid_address")
        }
    }
    
    @Test
    fun testRoundTripEncodeDecode() {
        val pubKeyBytes = ByteArray(32) { kotlin.random.Random.nextInt(256).toByte() }
        val pubKey = Ed25519PubKey(pubKeyBytes)
        val address = pubKey.account()
        
        val decodedPubKey = AccountDecoder.decodeAccountAddress(address)
        assertEquals(address, decodedPubKey.account())
    }
    
    @Test
    fun testValidAccountId() {
        val validAccountId = "A8yCiRddQZbmRew2bGLkjKP1Q5mEBvs5sSoXxS3UynxJVTiZ"
        
        // Should decode without throwing exception
        val pubKey = AccountDecoder.decodeAccountAddress(validAccountId)
        
        // Should produce same account ID when encoded back
        assertEquals(validAccountId, pubKey.account())
        
        // Should be able to use for signature verification (smoke test)
        val testData = "test".toByteArray()
        val testSig = ByteArray(64) { 0 }
        pubKey.verify(testData, testSig) // Should not throw
    }
}