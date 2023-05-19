package pt.isel.turngamesfw.repository.jdbi

import pt.isel.turngamesfw.domain.Token
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.repository.UserRepository
import java.time.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JdbiUserRepository(
    private val handle: Handle
): UserRepository {

    override fun createUser(username: String, passwordValidation: User.PasswordValidationInfo): Int {
        val userId = handle.createUpdate("insert into dbo.Users (username, password_validation) values (:username, :password_validation)")
            .bind("username", username)
            .bind("password_validation", passwordValidation.validationInfo)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

        handle.createQuery("select name from dbo.Games")
            .mapTo<String>()
            .forEach { nameGame ->
                handle.createUpdate("insert into dbo.UserStats (user_id, game_name, rating) values (:userId, :nameGame, 0)")
                    .bind("userId", userId)
                    .bind("nameGame", nameGame)
                    .execute()
            }

        return userId
    }

    override fun isUserStoredByUsername(username: String): Boolean =
        handle.createQuery("select count(*) from dbo.Users where username = :username")
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    override fun getUserByUsername(username: String): User? =
        handle.createQuery("select * from dbo.Users where username = :username")
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    override fun getUserById(id: Int): User? =
        handle.createQuery("select * from dbo.Users where id = :id")
            .bind("id", id)
            .mapTo<User>()
            .singleOrNull()

    override fun createToken(token: Token, maxTokens: Int) {
        // Delete tokens if are more than maxTokens
        handle.createUpdate("""
            delete from dbo.Tokens 
            where user_id = :user_id 
                and token_validation in (
                    select token_validation from dbo.Tokens where user_id = :user_id 
                        order by last_used_at desc offset :offset
                )
            """)
            .bind("user_id", token.userId)
            .bind("offset", maxTokens - 1)
            .execute()

        // Insert new token
        handle.createUpdate("""
                insert into dbo.Tokens(user_id, token_validation, created_at, last_used_at) 
                values (:user_id, :token_validation, :created_at, :last_used_at)
            """)
            .bind("user_id", token.userId)
            .bind("token_validation", token.tokenValidation.validationInfo)
            .bind("created_at", token.createdAt)
            .bind("last_used_at", token.lastUsedAt)
            .execute()
    }

    override fun getUserByToken(token: Token): User? =
        handle.createQuery("""select * from dbo.Users where id = :id""")
            .bind("id", token.userId)
            .mapTo<User>()
            .singleOrNull()

    override fun getTokenByTokenValidation(tokenValidation: Token.TokenValidationInfo): Token? =
        handle.createQuery("""select * from dbo.Tokens as users where token_validation = :validation_information""")
            .bind("validation_information", tokenValidation.validationInfo)
            .mapTo<Token>()
            .singleOrNull()

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        handle.createUpdate("""
                update dbo.Tokens
                set last_used_at = :last_used_at
                where token_validation = :validation_information
            """)
            .bind("last_used_at", now)
            .bind("validation_information", token.tokenValidation.validationInfo)
            .execute()
    }

    override fun getAllUsers(): List<User> =
        handle.createQuery("select * from dbo.Users")
            .mapTo<User>()
            .toSet()
            .toList()

    override fun updateStatus(id: Int, status: User.Status) {
        handle.createUpdate("""
                update dbo.Users
                set status = :status
                where id = :id
            """)
            .bind("status", status)
            .bind("id", id)
            .execute()
    }

    override fun getStatus(id: Int): User.Status? =
        handle.createQuery("select status from dbo.Users where id = :id")
            .bind("id", id)
            .mapTo<User.Status>()
            .singleOrNull()

}