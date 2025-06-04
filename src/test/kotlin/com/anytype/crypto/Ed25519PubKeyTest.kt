package com.anytype.crypto

import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.security.SecureRandom
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Ed25519PubKeyTest {
    
    @Test
    fun testConstructorValidatesKeySize() {
        assertThrows<IllegalArgumentException> {
            Ed25519PubKey(ByteArray(31))
        }
        
        assertThrows<IllegalArgumentException> {
            Ed25519PubKey(ByteArray(33))
        }
        
        Ed25519PubKey(ByteArray(32))
    }
    
    @Test
    fun testVerifySignature() {
        val keyPairGenerator = Ed25519KeyPairGenerator()
        keyPairGenerator.init(Ed25519KeyGenerationParameters(SecureRandom()))
        val keyPair = keyPairGenerator.generateKeyPair()
        
        val privateKey = keyPair.private as Ed25519PrivateKeyParameters
        val publicKey = keyPair.public as Ed25519PublicKeyParameters
        
        val message = "Test message".toByteArray()
        
        val signer = Ed25519Signer()
        signer.init(true, privateKey)
        signer.update(message, 0, message.size)
        val signature = signer.generateSignature()
        
        val pubKey = Ed25519PubKey(publicKey.encoded)
        assertTrue(pubKey.verify(message, signature))
        
        val wrongMessage = "Wrong message".toByteArray()
        assertFalse(pubKey.verify(wrongMessage, signature))
        
        val wrongSignature = ByteArray(64) { 0x00 }
        assertFalse(pubKey.verify(message, wrongSignature))
    }
    
    @Test
    fun testAccount() {
        val pubKeyBytes = ByteArray(32) { it.toByte() }
        val pubKey = Ed25519PubKey(pubKeyBytes)
        val account = pubKey.account()
        
        val decoded = StrKey.decode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, account)
        assertEquals(32, decoded.size)
        assertTrue(decoded.contentEquals(pubKeyBytes))
    }
}