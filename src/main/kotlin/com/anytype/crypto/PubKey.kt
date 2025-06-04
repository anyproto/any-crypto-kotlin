package com.anytype.crypto

interface PubKey {
    fun verify(data: ByteArray, sig: ByteArray): Boolean
    fun account(): String
}