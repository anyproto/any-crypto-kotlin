package com.anytype.crypto

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CRC16Test {
    
    @Test
    fun testChecksum() {
        val data = "hello world".toByteArray()
        val checksum = CRC16.checksum(data)
        
        assertEquals(2, checksum.size)
        assertTrue(CRC16.validate(data, checksum))
    }
    
    @Test
    fun testValidate() {
        val data = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val checksum = CRC16.checksum(data)
        
        assertTrue(CRC16.validate(data, checksum))
        
        val wrongChecksum = byteArrayOf(0x00, 0x00)
        assertTrue(!CRC16.validate(data, wrongChecksum))
    }
}