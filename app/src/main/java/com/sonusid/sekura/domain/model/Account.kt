package com.sonusid.sekura.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: Long = 0,
    val issuer: String,
    val accountName: String,
    val secretKey: String,
    val algorithm: String = "SHA1",
    val digits: Int = 6,
    val period: Int = 30
)
