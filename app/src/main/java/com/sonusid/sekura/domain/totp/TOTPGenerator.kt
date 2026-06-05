package com.sonusid.sekura.domain.totp

import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base32
import kotlin.math.pow

object TOTPGenerator {

    fun generateTOTP(
        secret: String,
        timestamp: Long = System.currentTimeMillis() / 1000,
        digits: Int = 6,
        period: Int = 30,
        algorithm: String = "SHA1"
    ): String {
        val counter = timestamp / period
        val key = try {
            Base32().decode(secret.uppercase().replace(" ", ""))
        } catch (e: Exception) {
            return "0".repeat(digits)
        }

        val hmacAlgorithm = when (algorithm.uppercase()) {
            "SHA1" -> "HmacSHA1"
            "SHA256" -> "HmacSHA256"
            "SHA512" -> "HmacSHA512"
            else -> "HmacSHA1"
        }

        val mac = Mac.getInstance(hmacAlgorithm)
        mac.init(SecretKeySpec(key, hmacAlgorithm))

        val buffer = ByteBuffer.allocate(8).putLong(counter).array()
        val hash = mac.doFinal(buffer)

        val offset = hash[hash.size - 1].toInt() and 0xf
        val binary = ((hash[offset].toInt() and 0x7f) shl 24) or
                ((hash[offset + 1].toInt() and 0xff) shl 16) or
                ((hash[offset + 2].toInt() and 0xff) shl 8) or
                (hash[offset + 3].toInt() and 0xff)

        val otp = binary % 10.0.pow(digits.toDouble()).toInt()
        return otp.toString().padStart(digits, '0')
    }

    fun getRemainingSeconds(period: Int = 30): Int {
        return (period - ((System.currentTimeMillis() / 1000) % period)).toInt()
    }
}
