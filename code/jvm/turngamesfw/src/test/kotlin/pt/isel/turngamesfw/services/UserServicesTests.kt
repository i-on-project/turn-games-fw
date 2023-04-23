package pt.isel.turngamesfw.services

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.security.crypto.password.PasswordEncoder
import pt.isel.turngamesfw.domain.*
import pt.isel.turngamesfw.repository.GameRepository
import pt.isel.turngamesfw.repository.TransactionManager
import pt.isel.turngamesfw.repository.UserRepository
import pt.isel.turngamesfw.services.results.*
import pt.isel.turngamesfw.utils.Clock
import pt.isel.turngamesfw.utils.Either
import pt.isel.turngamesfw.utils.TokenEncoder
import pt.isel.turngamesfw.utils.getTransactionManager
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertIs

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServicesTests {
    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var gameRepository: GameRepository
    
    @MockK
    private lateinit var passwordEncoder: PasswordEncoder
    
    @MockK
    private lateinit var tokenEncoder: TokenEncoder
    
    @MockK
    private lateinit var clock: Clock

    private lateinit var transactionManager: TransactionManager
    
    private lateinit var userServices: UserServices

    private fun pvi(pass: String) = User.PasswordValidationInfo(pass)
    private val pass1 = "pass1"
    private val pass2 = "pass2"
    private val user1 = User(1, "user1", pvi(pass1))
    private val user2 = User(2, "user2", pvi(pass2))

    @BeforeAll
    fun setUp() {
        MockKAnnotations.init(this)
        transactionManager = getTransactionManager(userRepository, gameRepository)
        userServices = UserServices(transactionManager, UserLogic(), passwordEncoder, tokenEncoder, clock)

        //Base requirements
        every { passwordEncoder.encode(any()) } returns "encodedPass"

        //User
        every { userRepository.isUserStoredByUsername(any()) } returns false
        every { userRepository.isUserStoredByUsername(user1.username) } returns true

        every { userRepository.getUserById(any()) } returns null
        every { userRepository.getUserById(1) } returns user1

        every { userRepository.getUserByUsername(any()) } returns null
        every { userRepository.getUserByUsername(user1.username) } returns user1

        every { userRepository.createUser(any(), any()) } returns 2

        //Get all users
        every { userRepository.getAllUsers() } returns listOf(user1)
        every { userRepository.getUserById(2) } returns user2

        //Status
        every { userRepository.getStatus(any()) } returns null
        every { userRepository.getStatus(user1.id) } returns User.Status.ONLINE
        every { userRepository.updateStatus(user1.id, any()) } returns Unit

        //Token
        every { userRepository.createToken(any(), any()) } returns Unit
        every { passwordEncoder.matches(any(), any()) } returns false
        every { passwordEncoder.matches(user1.passwordValidation.validationInfo, user1.passwordValidation.validationInfo) } returns true
        every { clock.now() } returns Instant.now()
        every { tokenEncoder.createValidationInformation(any()) } returns Token.TokenValidationInfo(user1.passwordValidation.validationInfo)

    }

    @Test
    fun `create user invalid arguments`() {
        val result = userServices.createUser("", "")
        assertEquals(Either.Left(UserCreationError.InvalidArguments), result)
    }

    @Test
    fun `create user already exists`() {
        val result = userServices.createUser(user1.username, pass1)
        assertEquals(Either.Left(UserCreationError.UserAlreadyExists), result)
    }

    @Test
    fun `create new user`() {
        val result = userServices.createUser("user2", "pass2")
        assertIs<Either.Right<User>>(result)
    }

    @Test
    fun `get user that doesn't exists by id`() {
        val result = userServices.getUserById(0)
        assertEquals(null, result)
    }

    @Test
    fun `get user that exists by id`() {
        val result = userServices.getUserById(1)
        assertIs<User>(result)
    }

    @Test
    fun `get user with blank username`() {
        val result = userServices.getUserByUsername("")
        assertEquals(null, result)
    } 

    @Test
    fun `get user that doesn't exists by username`() {
        val result = userServices.getUserByUsername("unknown")
        assertEquals(null, result)
    }

    @Test
    fun `get user that exists by username`() {
        val result = userServices.getUserByUsername(user1.username)
        assertIs<User>(result)
    }

    @Test
    fun `is user stored by username`() {
        val result = userServices.isUserStoredByUsername("user1")
        assertEquals(true, result)
    }

    @Test
    fun `get all users` () {
        val result = userServices.getAllUsers()
        assertIs<List<User>>(result)
    }           

    @Test
    fun `get status of unknown user`() {
        val result = userServices.getStatus(0)
        assertEquals(null, result)
    }

    @Test
    fun `update and get status`() {
        userServices.updateStatus(user1.id, User.Status.ONLINE)
        val result = userServices.getStatus(user1.id)

        assertEquals(User.Status.ONLINE, result)
    }

    @Test
    fun `create token with blank username or password`() {
        val result = userServices.createToken("", "")
        assertEquals(Either.Left(TokenCreationError.UserOrPasswordAreInvalid), result)
    }

    @Test
    fun `create token for unknown user`() {
        val result = userServices.createToken("unknown", "pass")
        assertEquals(Either.Left(TokenCreationError.UserOrPasswordAreInvalid), result)
    }

    @Test
    fun `create token with invalid password`() {
        val result = userServices.createToken(user1.username, "wrongPass")
        assertEquals(Either.Left(TokenCreationError.UserOrPasswordAreInvalid), result)
    }

    @Test
    fun `create token`() {
        val result = userServices.createToken(user1.username, pass1)
        assertIs<Either.Right<String>>(result)
    }

    @Test
    fun `get user with blank token`() {
        val result = userServices.getUserByToken("")
        assertEquals(null, result)
    }

    @Test
    fun `get user with badly formatted token`() {
        val result = userServices.getUserByToken("bad")
        assertEquals(null, result)
    }

    @Test
    fun `get user with invalid token`() {
        val result = userServices.getUserByToken("user1-bad-token")
        assertEquals(null, result)
    }

    @Test
    fun `get unknown user by token`() {
        val result = userServices.getUserByToken("user4-token")
        assertEquals(null, result)
    }
}