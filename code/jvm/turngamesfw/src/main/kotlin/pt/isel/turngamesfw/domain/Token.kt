package pt.isel.turngamesfw.domain

import java.time.Instant

data class Token(
    val tokenValidationInfo: TokenValidationInfo,
    val userId: Int,
    val createdAt: Instant,
    val lastUsedAt: Instant
) {
    data class TokenValidationInfo(
        val validationInfo: String
    )
}