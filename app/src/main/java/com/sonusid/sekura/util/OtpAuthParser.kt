package com.sonusid.sekura.util

import android.net.Uri
import com.sonusid.sekura.domain.model.Account

object OtpAuthParser {
    /**
     * Parses an otpauth:// URI into an Account object.
     * Example: otpauth://totp/GitHub:user?secret=ABC&issuer=GitHub
     */
    fun parse(uriString: String): Account? {
        val uri = try {
            Uri.parse(uriString)
        } catch (e: Exception) {
            return null
        }

        if (uri.scheme != "otpauth" || uri.host != "totp") return null

        val path = uri.path?.trimStart('/') ?: return null
        val parts = path.split(':')
        
        val issuerFromPath = if (parts.size > 1) parts[0] else null
        val accountName = if (parts.size > 1) parts[1] else parts[0]
        
        val secret = uri.getQueryParameter("secret") ?: return null
        val issuerFromQuery = uri.getQueryParameter("issuer")
        
        val issuer = issuerFromQuery ?: issuerFromPath ?: "Unknown"
        val algorithm = uri.getQueryParameter("algorithm") ?: "SHA1"
        val digits = uri.getQueryParameter("digits")?.toIntOrNull() ?: 6
        val period = uri.getQueryParameter("period")?.toIntOrNull() ?: 30

        return Account(
            issuer = issuer,
            accountName = accountName,
            secretKey = secret,
            algorithm = algorithm,
            digits = digits,
            period = period
        )
    }
}
