package com.anytype.crypto

import java.math.BigInteger

object Base58 {
    private const val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
    private val BASE = BigInteger.valueOf(58)
    
    fun encode(input: ByteArray): String {
        if (input.isEmpty()) {
            return ""
        }
        
        // Count leading zeros
        var leadingZeros = 0
        for (byte in input) {
            if (byte == 0.toByte()) {
                leadingZeros++
            } else {
                break
            }
        }
        
        // Convert to BigInteger (add 1 to ensure positive)
        val bytes = ByteArray(input.size + 1)
        System.arraycopy(input, 0, bytes, 1, input.size)
        var value = BigInteger(bytes)
        
        val result = StringBuilder()
        while (value > BigInteger.ZERO) {
            val divmod = value.divideAndRemainder(BASE)
            value = divmod[0]
            val remainder = divmod[1].toInt()
            result.insert(0, ALPHABET[remainder])
        }
        
        // Add leading '1's for leading zeros
        repeat(leadingZeros) {
            result.insert(0, '1')
        }
        
        return result.toString()
    }
    
    fun decode(input: String): ByteArray {
        if (input.isEmpty()) {
            return ByteArray(0)
        }
        
        // Count leading '1's (which represent leading zeros in bytes)
        var leadingOnes = 0
        for (char in input) {
            if (char == '1') {
                leadingOnes++
            } else {
                break
            }
        }
        
        // Decode the rest
        var value = BigInteger.ZERO
        for (char in input) {
            val digit = ALPHABET.indexOf(char)
            if (digit == -1) {
                throw IllegalArgumentException("Invalid Base58 character: $char")
            }
            value = value.multiply(BASE).add(BigInteger.valueOf(digit.toLong()))
        }
        
        // Convert to bytes
        val bytes = value.toByteArray()
        
        // Remove leading zero byte if added by BigInteger
        val stripSignByte = bytes.size > 1 && bytes[0] == 0.toByte()
        val start = if (stripSignByte) 1 else 0
        
        // Create result with leading zeros
        val result = ByteArray(leadingOnes + bytes.size - start)
        System.arraycopy(bytes, start, result, leadingOnes, bytes.size - start)
        
        return result
    }
}