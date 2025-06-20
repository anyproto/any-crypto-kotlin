package com.anytype.crypto

import org.komputing.kbase58.decodeBase58
import org.komputing.kbase58.encodeToBase58String

object StrKey {
    const val ACCOUNT_ADDRESS_VERSION_BYTE: Byte = 0x5B.toByte()
    
    class InvalidVersionByteException : Exception("Invalid version byte")
    class InvalidChecksumException : Exception("Invalid checksum")
    class InvalidLengthException(message: String) : Exception(message)
    
    fun decode(expected: Byte, src: String): ByteArray {
        val raw = decodeString(src)
        
        val version = raw[0]
        val vp = raw.sliceArray(0 until raw.size - 2)
        val payload = raw.sliceArray(1 until raw.size - 2)
        val checksum = raw.sliceArray(raw.size - 2 until raw.size)
        
        if (version != expected) {
            throw InvalidVersionByteException()
        }
        
        if (!CRC16.validate(vp, checksum)) {
            throw InvalidChecksumException()
        }
        
        return payload
    }
    
    fun encode(version: Byte, src: ByteArray): String {
        val raw = mutableListOf<Byte>()
        
        raw.add(version)
        raw.addAll(src.toList())
        
        val checksum = CRC16.checksum(raw.toByteArray())
        raw.addAll(checksum.toList())
        
        return raw.toByteArray().encodeToBase58String()
    }
    
    private fun decodeString(src: String): ByteArray {
        val raw = try {
            src.decodeBase58()
        } catch (e: Exception) {
            throw IllegalArgumentException("Base58 decode failed: ${e.message}")
        }
        
        if (raw.size < 3) {
            throw InvalidLengthException("Encoded value is ${raw.size} bytes; minimum valid length is 3")
        }
        
        return raw
    }
}