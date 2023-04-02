package pt.isel.turngamesfw.repository

import pt.isel.turngamesfw.domain.*
import java.time.Instant

interface UserRepository {

    fun createUser(username: String, passwordValidation: User.PasswordValidationInfo): String

    fun isUserStoredByUsername(username: String): Boolean

    fun getUserByUsername(username: String): User?

    fun getUserById(id: Int): User?

    fun createToken(token: String)

    fun getUserByToken(token: Token): User?

    fun getTokenByTokenValidation(tokenValidation: Token.TokenValidationInfo): Token?

    fun updateTokenLastUsed(token: Token, now: Instant)

    fun getAllUser(): List<User>

    fun updateStatus(id: Int, status: User.Status)

    fun getStatus(id: Int): User.Status?

    fun updateUser(user: User)

}