package pt.isel.turngamesfw.domain

import java.security.SecureRandom
import java.util.*

class UserLogic {

    companion object {
        private const val TOKEN_BYTE_SIZE = 256 / 8
    }

    fun generateToken(): String =
        ByteArray(TOKEN_BYTE_SIZE).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    fun canBeToken(token: String): Boolean = try {
        Base64.getUrlDecoder()
            .decode(token).size == TOKEN_BYTE_SIZE
    } catch (ex: IllegalArgumentException) {
        false
    }

}