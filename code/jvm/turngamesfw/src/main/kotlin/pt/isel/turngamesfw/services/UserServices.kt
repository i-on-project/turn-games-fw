package pt.isel.turngamesfw.services

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pt.isel.turngamesfw.domain.Token
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.domain.UserLogic
import pt.isel.turngamesfw.repository.TransactionManager
import pt.isel.turngamesfw.services.results.*
import pt.isel.turngamesfw.utils.Clock
import pt.isel.turngamesfw.utils.Either
import pt.isel.turngamesfw.utils.TokenEncoder
import java.time.Duration

@Component
class UserServices(
    private val transactionManager: TransactionManager,
    private val userLogic: UserLogic,
    private val passwordEncoder: PasswordEncoder,
    private val tokenEncoder: TokenEncoder,
    private val clock: Clock,
) {

    companion object {
        val TOKEN_ROLLING_TTL: Duration = Duration.ofHours(1)
        val TOKEN_TTL: Duration = Duration.ofDays(1)
        const val MAX_TOKENS: Int = 3
    }

    fun createUser(username: String, password: String): UserCreationResult {
        if (username.isBlank() || password.isBlank()) {
            return Either.Left(UserCreationError.InvalidArguments)
        }

        val passwordValidationInfo = User.PasswordValidationInfo(passwordEncoder.encode(password))

        return transactionManager.run {
            return@run if (it.usersRepository.isUserStoredByUsername(username)) {
                Either.Left(UserCreationError.UserAlreadyExists)
            } else {
                Either.Right(it.usersRepository.createUser(username, passwordValidationInfo))
            }
        }
    }
    
    fun isUserStoredByUsername(username: String): Boolean {
        return transactionManager.run {
            return@run it.usersRepository.isUserStoredByUsername(username)
        }
    }

    fun getUserByUsername(username: String): User? {
        return transactionManager.run {
            return@run it.usersRepository.getUserByUsername(username)
        }
    }

    fun getUserById(id: Int): User? {
        return transactionManager.run {
            return@run it.usersRepository.getUserById(id)
        }
    }
    
    fun createToken(username: String, password: String): TokenCreationResult {
        if (username.isBlank() || password.isBlank()) {
            Either.Left(TokenCreationError.InvalidArguments)
        }

        return transactionManager.run {
            val user = it.usersRepository.getUserByUsername(username) ?: return@run Either.Left(TokenCreationError.UserOrPasswordAreInvalid)

            if (!passwordEncoder.matches(password, user.passwordValidation.validationInfo)) {
                return@run Either.Left(TokenCreationError.UserOrPasswordAreInvalid)
            }

            val token = userLogic.generateToken()
            val now = clock.now()
            val newToken = Token(tokenEncoder.createValidationInformation(token), user.id, now, now)

            it.usersRepository.createToken(newToken, MAX_TOKENS)
            return@run Either.Right(token)
        }
    }

    fun getUserByToken(token: String): User? {
        if (!userLogic.canBeToken(token)) { return null }

        return transactionManager.run {
            val usersRepository = it.usersRepository
            val tokenValidationInfo = tokenEncoder.createValidationInformation(token)
            val token = usersRepository.getTokenByTokenValidation(tokenValidationInfo)

            if (token == null || isTokenStillValid(token)) return@run null

            usersRepository.updateTokenLastUsed(token, clock.now())
            return@run it.usersRepository.getUserByToken(token)
        }
    }

    fun getTokenByTokenValidation(tokenValidation: Token.TokenValidationInfo): Token? {
        return transactionManager.run {
            return@run it.usersRepository.getTokenByTokenValidation(tokenValidation)
        }
    }

    fun getAllUsers() : List<User>? {
        return transactionManager.run {
            return@run it.usersRepository.getAllUsers()
        }
    }

    private fun isTokenStillValid(token: Token): Boolean {
        val now = clock.now()
        return now.isBefore(token.createdAt.plus(TOKEN_TTL)) && now.isBefore(token.lastUsedAt.plus(TOKEN_ROLLING_TTL))
    }

    fun updateStatus(id: Int, status: User.Status) {
        transactionManager.run {
            it.usersRepository.updateStatus(id, status)
        }
    }

    fun getStatus(id: Int): User.Status? {
        return transactionManager.run {
            return@run it.usersRepository.getStatus(id)
        }
    }
}