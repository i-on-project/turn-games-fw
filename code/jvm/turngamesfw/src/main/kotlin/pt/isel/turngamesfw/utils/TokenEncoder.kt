package pt.isel.turngamesfw.utils

import pt.isel.turngamesfw.domain.Token.TokenValidationInfo

interface TokenEncoder {
    fun createValidationInformation(token: String): TokenValidationInfo
    fun validate(validationInfo: TokenValidationInfo, token: String): Boolean
}