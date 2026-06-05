package com.sonusid.sekura.domain.totp

import org.junit.Assert.assertEquals
import org.junit.Test

class TOTPGeneratorTest {

    @Test
    fun testGenerateTOTP() {
        // Test vector from RFC 6238
        // Secret: "12345678901234567890" in Hex
        // Base32: GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ
        val secret = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ"
        
        // T=59
        assertEquals("287082", TOTPGenerator.generateTOTP(secret, 59))
        
        // T=1111111109
        assertEquals("081804", TOTPGenerator.generateTOTP(secret, 1111111109))
        
        // T=1111111111
        assertEquals("050471", TOTPGenerator.generateTOTP(secret, 1111111111))
        
        // T=1234567890
        assertEquals("005924", TOTPGenerator.generateTOTP(secret, 1234567890))
        
        // T=2000000000
        assertEquals("279037", TOTPGenerator.generateTOTP(secret, 2000000000))
    }
}
