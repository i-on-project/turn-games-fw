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

    private val u1 = User(1, "u1", User.PasswordValidationInfo("p1"))
    private val u2 = User(2, "u2", User.PasswordValidationInfo("p2"))
    private val createdAt = Instant.now()
    private val lastUsedAt = Instant.now() 
    private val t1 = Token(Token.TokenValidationInfo("p1"), 1, createdAt, lastUsedAt)

    @BeforeAll
    fun setUp() {
        MockKAnnotations.init(this)
        transactionManager = getTransactionManager(userRepository, gameRepository)
        userServices = UserServices(transactionManager, UserLogic(), passwordEncoder, tokenEncoder, clock)
        every { userRepository.createUser(any(), any()) } returns 2
        every { userRepository.createUser("u1", any()) } returns 1
        every { userRepository.getUserById(any()) } returns null
        every { userRepository.getUserById(1) } returns u1
        every { userRepository.isUserStoredByUsername(any()) } returns false
        every { userRepository.isUserStoredByUsername("u1") } returns true
        every { passwordEncoder.encode(any()) } returns "encoded_password"
        every { userRepository.getUserByUsername(any()) } returns null
        every { userRepository.getUserByUsername("u1") } returns u1
        every { userRepository.getTokenByTokenValidation(Token.TokenValidationInfo("p1")) } returns t1
    }

    @Test
    fun `create user invalid arguments`() {
        val result = userServices.createUser("", "")
        assertEquals(Either.Left(UserCreationError.InvalidArguments), result)
    }

    @Test
    fun `create user already exists`() {
        val result = userServices.createUser("u1", "p1")
        assertEquals(Either.Left(UserCreationError.UserAlreadyExists), result)
    }

    @Test
    fun `create new user`(){
        val result = userServices.createUser("u2", "p2")
        assertEquals(Either.Right(u2.id), result)
    }

    @Test
    fun `is user stored by username`() {
        val result = userServices.isUserStoredByUsername("u1")
        assertEquals(true, result)
        val result2 = userServices.isUserStoredByUsername("u2")
        assertEquals(false, result2)
    }

    @Test
    fun `get user by username`() {
        val result = userServices.getUserByUsername("u1")
        assertEquals(u1, result)
        val result2 = userServices.getUserByUsername("u2")
        assertEquals(null, result2)
    }

    @Test
    fun `get user by id`() {
        val result = userServices.getUserById(1)
        assertEquals(u1, result)
        val result2 = userServices.getUserById(2)
        assertEquals(null, result2)
    }

    @Test  
    fun `create token invalid arguments`() {
        val result = userServices.createToken("", "")
        assertEquals(Either.Left(TokenCreationError.InvalidArguments), result)
    }

    @Test
    fun `create token user or password are invalid`() {
        every { userRepository.getUserByUsername(any()) } returns null
        val result = userServices.createToken("u1", "p1")
        assertEquals(Either.Left(TokenCreationError.UserOrPasswordAreInvalid), result)
    }

    @Test
    fun `create token with success`() {
        val result = userServices.createToken("u1", "p1")
        assertEquals(Either.Right("encoded_token"), result)
    }

    @Test
    fun `get user by token`() {
        val result = userServices.getUserByToken("encoded_token")
        assertEquals(u1, result)
    }

    @Test
    fun `get token by token validation`() {
        val result = userServices.getTokenByTokenValidation(Token.TokenValidationInfo("p1"))
        assertEquals(Token(Token.TokenValidationInfo("p1"), u1.id, Instant.now(), Instant.now()), result)
    }

    //TODO Fix current tests and do more tests
}