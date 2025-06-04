# Any Crypto Kotlin

A Kotlin library for Ed25519 cryptographic operations, including account address encoding/decoding and signature verification.

[![](https://jitpack.io/v/anyproto/any-crypto-kotlin.svg)](https://jitpack.io/#anyproto/any-crypto-kotlin)

## Features

- Decode Anytype account addresses to Ed25519 public keys
- Verify Ed25519 signatures
- Encode Ed25519 public keys to Anytype account format
- CRC16 checksum validation
- Base58 encoding/decoding with StrKey format

## Installation

### Using JitPack

Add JitPack repository to your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.anyproto:any-crypto-kotlin:1.0.0")
}
```

## Usage

### Decoding Account Address

```kotlin
import com.anytype.crypto.AccountDecoder

// Decode an account address to get the public key
val accountAddress = "MDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDA4djV6"
val pubKey = AccountDecoder.decodeAccountAddress(accountAddress)

// Get the account representation back
val address = pubKey.account()
println("Account: $address")
```

### Verifying Signatures

```kotlin
import com.anytype.crypto.AccountDecoder
import org.bouncycastle.util.encoders.Hex

// Decode account to get public key
val pubKey = AccountDecoder.decodeAccountAddress(accountAddress)

// Data that was signed
val data = "Hello, World!".toByteArray()

// Ed25519 signature (64 bytes)
val signature = Hex.decode("...")  // Your signature hex string

// Verify the signature
val isValid = pubKey.verify(data, signature)
println("Signature valid: $isValid")
```

### Working with Ed25519 Keys Directly

```kotlin
import com.anytype.crypto.Ed25519PubKey

// Create from raw 32-byte public key
val publicKeyBytes = ByteArray(32) // Your 32-byte Ed25519 public key
val pubKey = Ed25519PubKey(publicKeyBytes)

// Encode to account address format
val accountAddress = pubKey.account()

// Verify signatures
val isValid = pubKey.verify(data, signature)
```

### Complete Example

```kotlin
import com.anytype.crypto.AccountDecoder
import com.anytype.crypto.Ed25519PubKey
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import java.security.SecureRandom

fun main() {
    // Generate a key pair for testing
    val keyPairGenerator = Ed25519KeyPairGenerator()
    keyPairGenerator.init(Ed25519KeyGenerationParameters(SecureRandom()))
    val keyPair = keyPairGenerator.generateKeyPair()
    
    val privateKey = keyPair.private as Ed25519PrivateKeyParameters
    val publicKey = keyPair.public as Ed25519PublicKeyParameters
    
    // Create PubKey from raw bytes
    val pubKey = Ed25519PubKey(publicKey.encoded)
    
    // Get account address
    val accountAddress = pubKey.account()
    println("Account Address: $accountAddress")
    
    // Sign some data
    val message = "Test message".toByteArray()
    val signer = Ed25519Signer()
    signer.init(true, privateKey)
    signer.update(message, 0, message.size)
    val signature = signer.generateSignature()
    
    // Decode account address back to public key
    val decodedPubKey = AccountDecoder.decodeAccountAddress(accountAddress)
    
    // Verify signature
    val isValid = decodedPubKey.verify(message, signature)
    println("Signature valid: $isValid")
}
```

## API Reference

### AccountDecoder

- `decodeAccountAddress(address: String): PubKey` - Decodes an Anytype account address to a PubKey

### PubKey Interface

- `verify(data: ByteArray, sig: ByteArray): Boolean` - Verifies an Ed25519 signature
- `account(): String` - Returns the Anytype account address representation

### Ed25519PubKey

- Constructor: `Ed25519PubKey(pubKey: ByteArray)` - Creates from 32-byte Ed25519 public key
- Implements `PubKey` interface

## License

MIT