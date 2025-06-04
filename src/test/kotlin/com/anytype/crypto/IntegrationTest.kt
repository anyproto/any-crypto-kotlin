package com.anytype.crypto

import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.bouncycastle.util.encoders.Hex
import org.junit.jupiter.api.Test
import java.security.SecureRandom
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IntegrationTest {
    
    @Test
    fun testFullWorkflow() {
        // Generate key pair
        val keyPairGenerator = Ed25519KeyPairGenerator()
        keyPairGenerator.init(Ed25519KeyGenerationParameters(SecureRandom()))
        val keyPair = keyPairGenerator.generateKeyPair()
        
        val privateKey = keyPair.private as Ed25519PrivateKeyParameters
        val publicKey = keyPair.public as Ed25519PublicKeyParameters
        
        // Create PubKey and get account address
        val originalPubKey = Ed25519PubKey(publicKey.encoded)
        val accountAddress = originalPubKey.account()
        
        println("Generated account address: $accountAddress")
        
        // Decode account address back
        val decodedPubKey = AccountDecoder.decodeAccountAddress(accountAddress)
        
        // Verify they produce the same account address
        assertEquals(accountAddress, decodedPubKey.account())
        
        // Sign a message
        val message = "Hello, Anytype!".toByteArray()
        val signer = Ed25519Signer()
        signer.init(true, privateKey)
        signer.update(message, 0, message.size)
        val signature = signer.generateSignature()
        
        println("Message: ${String(message)}")
        println("Signature: ${Hex.toHexString(signature)}")
        
        // Verify with original key
        assertTrue(originalPubKey.verify(message, signature))
        
        // Verify with decoded key
        assertTrue(decodedPubKey.verify(message, signature))
        
        // Verify fails with wrong message
        assertFalse(decodedPubKey.verify("Wrong message".toByteArray(), signature))
        
        // Verify fails with wrong signature
        val wrongSignature = ByteArray(64) { 0xFF.toByte() }
        assertFalse(decodedPubKey.verify(message, wrongSignature))
    }
    
    @Test
    fun testKnownTestVector() {
        // Test with a known public key
        val publicKeyHex = "d75a980182b10ab7d54bfed3c964073a0ee172f3daa62325af021a68f707511a"
        val publicKeyBytes = Hex.decode(publicKeyHex)
        
        val pubKey = Ed25519PubKey(publicKeyBytes)
        val accountAddress = pubKey.account()
        
        println("Known public key: $publicKeyHex")
        println("Account address: $accountAddress")
        
        // Decode it back
        val decoded = AccountDecoder.decodeAccountAddress(accountAddress)
        assertEquals(accountAddress, decoded.account())
        
        // Test signature verification with known test vector
        val message = Hex.decode("")
        val signature = Hex.decode(
            "e5564300c360ac729086e2cc806e828a84877f1eb8e5d974d873e06522490155" +
            "5fb8821590a33bacc61e39701cf9b46bd25bf5f0595bbe24655141438e7a100b"
        )
        
        assertTrue(pubKey.verify(message, signature))
    }
    
    @Test
    fun testMultipleMessages() {
        val keyPairGenerator = Ed25519KeyPairGenerator()
        keyPairGenerator.init(Ed25519KeyGenerationParameters(SecureRandom()))
        val keyPair = keyPairGenerator.generateKeyPair()
        
        val privateKey = keyPair.private as Ed25519PrivateKeyParameters
        val publicKey = keyPair.public as Ed25519PublicKeyParameters
        
        val pubKey = Ed25519PubKey(publicKey.encoded)
        val accountAddress = pubKey.account()
        val decodedPubKey = AccountDecoder.decodeAccountAddress(accountAddress)
        
        val messages = listOf(
            "First message",
            "Second message with more content",
            "🚀 Unicode message",
            "",
            "a".repeat(1000)
        )
        
        for (messageStr in messages) {
            val message = messageStr.toByteArray()
            
            val signer = Ed25519Signer()
            signer.init(true, privateKey)
            signer.update(message, 0, message.size)
            val signature = signer.generateSignature()
            
            assertTrue(decodedPubKey.verify(message, signature), 
                "Failed to verify signature for message: $messageStr")
        }
    }
    
    @Test
    fun testAccountAddressFormat() {
        val pubKey = ByteArray(32) { it.toByte() }
        val account = StrKey.encode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, pubKey)
        
        // Account address should be base58 encoded
        assertTrue(account.all { it in "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz" })
        
        // Should be able to decode back
        val decoded = StrKey.decode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, account)
        assertTrue(decoded.contentEquals(pubKey))
    }
}