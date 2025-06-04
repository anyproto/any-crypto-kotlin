package com.anytype.crypto

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals

class StrKeyTest {
    
    @Test
    fun testEncodeAndDecode() {
        val testData = ByteArray(32) { it.toByte() }
        val encoded = StrKey.encode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, testData)
        val decoded = StrKey.decode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, encoded)
        
        assertContentEquals(testData, decoded)
    }
    
    @Test
    fun testDecodeWithWrongVersion() {
        val testData = ByteArray(32) { 0x42 }
        val encoded = StrKey.encode(0x01, testData)
        
        assertThrows<StrKey.InvalidVersionByteException> {
            StrKey.decode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, encoded)
        }
    }
    
    @Test
    fun testDecodeInvalidBase58() {
        assertThrows<IllegalArgumentException> {
            StrKey.decode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, "invalid!@#$%")
        }
    }
    
    @Test
    fun testDecodeTooShort() {
        assertThrows<StrKey.InvalidLengthException> {
            StrKey.decode(StrKey.ACCOUNT_ADDRESS_VERSION_BYTE, "AB")
        }
    }
}