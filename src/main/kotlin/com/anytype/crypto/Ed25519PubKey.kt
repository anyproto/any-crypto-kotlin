package com.anytype.crypto

import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer

class Ed25519PubKey(private val pubKey: ByteArray) : PubKey {
    
    init {
        require(pubKey.size == 32) { "Ed25519 public key must be 32 bytes" }
    }
    
    override fun verify(data: ByteArray, sig: ByteArray): Boolean {
        return try {
            val publicKeyParams = Ed25519PublicKeyParameters(pubKey, 0)
            val verifier = Ed25519Signer()
            verifier.init(false, publicKeyParams)
            verifier.update(data, 0, data.size)
            verifier.verifySignature(sig)
        } catch (e: Exception) {
            false
        }
    }
    
    override fun account(): String {
        return StrKey.encode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, pubKey)
    }
}