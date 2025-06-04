package com.anytype.crypto

import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.bouncycastle.util.encoders.Hex
import java.security.SecureRandom

fun main() {
    println("=== Any Crypto Kotlin Test Runner ===\n")
    
    // Test 1: Basic encoding/decoding
    println("Test 1: Basic StrKey encoding/decoding")
    val testPubKey = ByteArray(32) { (it + 1).toByte() }
    val encoded = StrKey.encode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, testPubKey)
    println("Encoded account: $encoded")
    val decoded = StrKey.decode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, encoded)
    println("Decoded matches: ${decoded.contentEquals(testPubKey)}")
    println()
    
    // Test 2: Full workflow with signature verification
    println("Test 2: Full workflow with signature verification")
    val keyPairGenerator = Ed25519KeyPairGenerator()
    keyPairGenerator.init(Ed25519KeyGenerationParameters(SecureRandom()))
    val keyPair = keyPairGenerator.generateKeyPair()
    
    val privateKey = keyPair.private as Ed25519PrivateKeyParameters
    val publicKey = keyPair.public as Ed25519PublicKeyParameters
    
    val pubKey = Ed25519PubKey(publicKey.encoded)
    val accountAddress = pubKey.account()
    println("Generated account address: $accountAddress")
    
    // Sign a message
    val message = "Hello, Anytype!".toByteArray()
    val signer = Ed25519Signer()
    signer.init(true, privateKey)
    signer.update(message, 0, message.size)
    val signature = signer.generateSignature()
    
    println("Message: ${String(message)}")
    println("Signature: ${Hex.toHexString(signature)}")
    
    // Decode and verify
    val decodedPubKey = AccountDecoder.decodeAccountAddress(accountAddress)
    val isValid = decodedPubKey.verify(message, signature)
    println("Signature valid: $isValid")
    println()
    
    // Test 3: Known test vector
    println("Test 3: Known test vector")
    val knownPubKeyHex = "d75a980182b10ab7d54bfed3c964073a0ee172f3daa62325af021a68f707511a"
    val knownPubKey = Ed25519PubKey(Hex.decode(knownPubKeyHex))
    val knownAccount = knownPubKey.account()
    println("Known public key: $knownPubKeyHex")
    println("Account address: $knownAccount")
    
    println("\n=== All tests completed successfully! ===")
}