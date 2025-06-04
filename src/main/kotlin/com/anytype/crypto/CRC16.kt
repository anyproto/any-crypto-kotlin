package com.anytype.crypto

object CRC16 {
    private const val POLY = 0x1021
    
    fun checksum(data: ByteArray): ByteArray {
        var crc = 0
        
        for (byte in data) {
            crc = crc xor (byte.toInt() and 0xFF shl 8)
            
            for (i in 0 until 8) {
                if (crc and 0x8000 != 0) {
                    crc = (crc shl 1) xor POLY
                } else {
                    crc = crc shl 1
                }
            }
        }
        
        return byteArrayOf(
            (crc and 0xFF).toByte(),
            ((crc shr 8) and 0xFF).toByte()
        )
    }
    
    fun validate(data: ByteArray, checksum: ByteArray): Boolean {
        val calculated = checksum(data)
        return calculated.contentEquals(checksum)
    }
}