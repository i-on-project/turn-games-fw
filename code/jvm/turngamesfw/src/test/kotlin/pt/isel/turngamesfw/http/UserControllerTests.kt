package pt.isel.turngamesfw.http

import com.google.gson.Gson
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.http.controller.UserController
import pt.isel.turngamesfw.http.model.RegisterInputModel
import pt.isel.turngamesfw.http.pipeline.AuthenticationInterceptor
import pt.isel.turngamesfw.http.pipeline.AuthorizationHeaderProcessor
import pt.isel.turngamesfw.http.pipeline.UserArgumentResolver
import pt.isel.turngamesfw.services.UserServices
import pt.isel.turngamesfw.services.results.*
import pt.isel.turngamesfw.utils.Either

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(UserController::class, AuthenticationInterceptor::class)
class UserControllerTests {

    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var userService: UserServices

    @MockkBean
    private lateinit var authorizationHeaderProcessor: AuthorizationHeaderProcessor

    private val contentTypeSiren = "application/vnd.siren+json"

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(UserController(userService))
            .addInterceptors(AuthenticationInterceptor(authorizationHeaderProcessor))
            .setCustomArgumentResolvers(UserArgumentResolver())
            .build()

        every { authorizationHeaderProcessor.process(any()) } returns User(
            1,
            "User1",
            User.PasswordValidationInfo("12345"),
        )
    }

    @Test
    fun `register user`() {
        val registerUser = RegisterInputModel("user1","pass1")
        val user = User(1, registerUser.username, User.PasswordValidationInfo(registerUser.password))

        every { userService.createUser("user1", "pass1") } returns Either.Right(user)

        val body = "{\"class\":[\"register\"],\"properties\":{\"id\":1,\"username\":\"user1\",\"status\":\"OFFLINE\"}}"

        mockMvc.perform(
            post(Uris.User.REGISTER)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(Gson().toJson(registerUser))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(contentTypeSiren))
            .andExpect(content().json(body))
    }

    @Test
    fun `do login`() {
        every { userService.createToken("user1", "pass1") } returns Either.Right("newToken")
        val body = "{\"class\":[\"login\"],\"properties\":{\"token\":\"newToken\"}}"
        mockMvc.perform(
            post(Uris.User.LOGIN)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"username\":\"user1\",\"password\":\"pass1\"}")
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(contentTypeSiren))
            .andExpect(content().json(body))
    }

    @Test
    fun `get user by id`() {
        val user = User(1, "User1", User.PasswordValidationInfo("12345"))
        every { userService.getUserById(1) } returns user
        val body = "{\"class\":[\"user\"],\"properties\":{\"id\":1,\"username\":\"User1\",\"status\":\"OFFLINE\"}}"
        mockMvc.perform(
            get(Uris.User.byId("1"))
        )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(contentTypeSiren))
            .andExpect(content().json(body))
    }
}
