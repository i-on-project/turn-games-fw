package pt.isel.turngamesfw.repository

import pt.isel.turngamesfw.domain.*
import java.time.Instant

interface UserRepository {

    fun createUser(username: String, passwordValidation: User.PasswordValidationInfo): Int

    fun getUserById(id: Int): User?

    fun getUserByUsername(username: String): User?

    fun isUserStoredByUsername(username: String): Boolean

    fun getAllUsers(): List<User>

    fun updateStatus(id: Int, status: User.Status)

    fun getStatus(id: Int): User.Status?

    fun createToken(token: Token, maxTokens: Int)

    fun getUserByToken(token: Token): User?

    fun getTokenByTokenValidation(tokenValidation: Token.TokenValidationInfo): Token?

    fun updateTokenLastUsed(token: Token, now: Instant)

}