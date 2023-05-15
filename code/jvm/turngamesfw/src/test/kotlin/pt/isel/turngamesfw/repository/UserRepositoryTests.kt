package pt.isel.turngamesfw.repository

import org.jdbi.v3.core.Handle
import org.junit.jupiter.api.Test
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
    private data class Init(val handle: Handle) {
        val userRepo = JdbiUserRepository(handle)
        val userLogic = UserLogic()
        val tokenEncoder = Sha256TokenEncoder()
        val clock = object : Clock { override fun now() = Instant.now().truncatedTo(ChronoUnit.SECONDS) }

        private fun pvi(p: String) = User.PasswordValidationInfo(p)

        val user = User(userRepo.createUser("user", pvi("pass")), "user", pvi("pass"))
        val users = (0..19).map { User(userRepo.createUser("user$it", pvi("pass$it")), "user$it", pvi("pass$it")) }

        val tokenString = userLogic.generateToken()
        val tokenValidationInfo = tokenEncoder.createValidationInformation(tokenString)
        val token = Token(tokenValidationInfo, user.id, clock.now(), clock.now())
        init {
            userRepo.createToken(token, 3)
        }
    }

    @Test
    fun `store user in repository`() {
        testWithHandleAndRollback { handle ->
            val i = Init(handle)

            val playerName = "playerTest"
            val pvi = User.PasswordValidationInfo("123")

            val idPlayer = i.userRepo.createUser(playerName, pvi)
            assertEquals(true, i.userRepo.isUserStoredByUsername(playerName))
        }
    }

    @Test
    fun `get user inserted by username`() {
        testWithHandleAndRollback { handle ->
            val i = Init(handle)

            assertEquals(true, i.userRepo.isUserStoredByUsername(i.user.username))
        }
    }

    @Test
    fun `get user inserted by id`() {
        testWithHandleAndRollback { handle ->
            val i = Init(handle)

            val playerName = "playerTest"
            val pvi = User.PasswordValidationInfo("123")
            val idPlayer = i.userRepo.createUser(playerName, pvi)

            val player = User(idPlayer, playerName, pvi)
            assertEquals(player, i.userRepo.getUserById(idPlayer))
        }
    }

    @Test
    fun `check if player as the correct username`() {
        testWithHandleAndRollback { handle ->
            val i = Init(handle)

            assertEquals(i.user.username, i.userRepo.getUserByUsername(i.user.username)?.username)
        }
    }

    @Test
    fun `create token for playerTest`() {
        testWithHandleAndRollback { handle ->
            val i = Init(handle)
        
            val tokenString = i.userLogic.generateToken()
            val tokenValidationInfo = i.tokenEncoder.createValidationInformation(tokenString)
            val now = i.clock.now()
            val token = Token(tokenValidationInfo, i.user.id, now, now)
            i.userRepo.createToken(token, 3)

            assertEquals(token, i.userRepo.getTokenByTokenValidation(tokenValidationInfo))

            val tokenTest = i.userRepo.getTokenByTokenValidation(tokenValidationInfo)
            assertEquals(token, tokenTest)
        }
    }

    @Test
    fun `update token last use`() {
        testWithHandleAndRollback { handle ->
            val i = Init(handle)

            val now = i.clock.now()
            i.userRepo.updateTokenLastUsed(i.token, now)

            // Check if the token is updated
            assertEquals(now, i.userRepo.getTokenByTokenValidation(i.tokenValidationInfo)?.lastUsedAt)
        }
    }

    @Test
    fun `update status`() {
        testWithHandleAndRollback { handle ->
            val i = Init(handle)

            i.userRepo.updateStatus(i.user.id, User.Status.ONLINE)
            assertEquals(User.Status.ONLINE, i.userRepo.getStatus(i.user.id))
        }
    }
}