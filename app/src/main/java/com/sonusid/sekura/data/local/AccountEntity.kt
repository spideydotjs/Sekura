package com.sonusid.sekura.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sonusid.sekura.domain.model.Account

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val issuer: String,
    val accountName: String,
    val secretKey: String,
    val algorithm: String,
    val digits: Int,
    val period: Int
)

fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        issuer = issuer,
        accountName = accountName,
        secretKey = secretKey,
        algorithm = algorithm,
        digits = digits,
        period = period
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        issuer = issuer,
        accountName = accountName,
        secretKey = secretKey,
        algorithm = algorithm,
        digits = digits,
        period = period
    )
}
