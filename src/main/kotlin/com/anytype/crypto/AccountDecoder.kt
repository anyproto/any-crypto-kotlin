package com.anytype.crypto

object AccountDecoder {
    
    fun decodeAccountAddress(address: String): PubKey {
        val pubKeyRaw = StrKey.decode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, address)
        return unmarshalEd25519PublicKey(pubKeyRaw)
    }
    
    private fun unmarshalEd25519PublicKey(data: ByteArray): PubKey {
        if (data.size != 32) {
            throw IllegalArgumentException("Expect ed25519 public key data size to be 32")
        }
        return Ed25519PubKey(data)
    }
}