package com.anytype.crypto

import org.junit.jupiter.api.Test
import org.komputing.kbase58.decodeBase58
import org.komputing.kbase58.encodeToBase58String
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals

class Base58Test {
    
    @Test
    fun testEncode() {
        val testCases = mapOf(
            "test".toByteArray() to "3yZe7d",
            "hello world".toByteArray() to "StV1DL6CwTryKyV",
            "".toByteArray() to "",
            byteArrayOf(0) to "1",
            byteArrayOf(0, 0) to "11",
            byteArrayOf(0, 0, 0) to "111"
        )
        
        testCases.forEach { (input, expected) ->
            val encoded = input.encodeToBase58String()
            assertEquals(expected, encoded, "Failed for input: ${String(input)}")
        }
    }
    
    @Test
    fun testDecode() {
        val testCases = mapOf(
            "3yZe7d" to "test".toByteArray(),
            "StV1DL6CwTryKyV" to "hello world".toByteArray(),
            "" to byteArrayOf(),
            "1" to byteArrayOf(0),
            "11" to byteArrayOf(0, 0),
            "111" to byteArrayOf(0, 0, 0)
        )
        
        testCases.forEach { (input, expected) ->
            val decoded = input.decodeBase58()
            assertContentEquals(expected, decoded, "Failed for input: $input")
        }
    }
    
    @Test
    fun testRoundTrip() {
        val testData = listOf(
            "test".toByteArray(),
            "hello world".toByteArray(),
            "The quick brown fox jumps over the lazy dog".toByteArray(),
            ByteArray(32) { it.toByte() },
            ByteArray(100) { (it * 7).toByte() }
        )
        
        testData.forEach { data ->
            val encoded = data.encodeToBase58String()
            val decoded = encoded.decodeBase58()
            assertContentEquals(data, decoded, "Round trip failed for data of length ${data.size}")
        }
    }
}