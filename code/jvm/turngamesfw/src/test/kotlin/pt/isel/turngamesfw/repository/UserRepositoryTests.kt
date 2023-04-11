package pt.isel.turngamesfw.repository

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import pt.isel.turngamesfw.domain.Token
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.domain.UserLogic
import pt.isel.turngamesfw.repository.jdbi.JdbiUserRepository
import pt.isel.turngamesfw.utils.Clock
import pt.isel.turngamesfw.utils.Sha256TokenEncoder
import pt.isel.turngamesfw.utils.testWithHandleAndRollback
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class UserRepositoryTests {

    @Test
    fun `all tests`(): Unit = testWithHandleAndRollback { handle ->

        // Get objects
        val userRepo = JdbiUserRepository(handle)
        val userLogic = UserLogic()
        val tokenEncoder = Sha256TokenEncoder()
        val clock = object : Clock {
            override fun now() = Instant.now().truncatedTo(ChronoUnit.SECONDS)
        }

        // Store user in repository
        val idPlayer = userRepo.createUser("playerTest", User.PasswordValidationInfo("123"))

        // Check if exist user by username
        assertEquals(true, userRepo.isUserStoredByUsername("playerTest"), "Error in createUser or isUserStoredByUsername")

        // Get user inserted by username
        var player = userRepo.getUserByUsername("playerTest") ?: fail("getUserByUsername user must exist")

        // Check if player as the correct username
        assertEquals("playerTest", player.username, "Error getting the right user by username")

        // Get user inserted by id
        player = userRepo.getUserById(idPlayer) ?: fail("getUserById user must exist")

        // Check if player as the correct username
        assertEquals("playerTest", player.username, "Error getting the right user by id")

        // Create token for playerTest
        val tokenString = userLogic.generateToken()
        val tokenValidationInfo = tokenEncoder.createValidationInformation(tokenString)
        var now = clock.now()
        val token = Token(tokenValidationInfo, idPlayer, now, now)
        userRepo.createToken(token, 3)

        // Get user by token inserted
        val user = userRepo.getUserByToken(token) ?: fail("getUserByToken user must exist")

        // Check if user as the correct username
        assertEquals("playerTest", user.username, "Error getting the right user by token")

        // Get user by token validation
        val tokenTest = userRepo.getTokenByTokenValidation(tokenValidationInfo)

        // Check if the token is the same
        assertEquals(token, tokenTest, "Error getting the right token by token validation")

        // Update token last use
        now = clock.now()
        userRepo.updateTokenLastUsed(token, now)

        // Check if the token is updated
        assertEquals(now, userRepo.getTokenByTokenValidation(tokenValidationInfo)?.lastUsedAt, "Error updating token last used")

        // Update status of user
        userRepo.updateStatus(idPlayer, User.Status.ONLINE)

        // Check if the user is updated
        assertEquals(User.Status.ONLINE, userRepo.getStatus(idPlayer), "Error updating user status, or getting status")

    }

}