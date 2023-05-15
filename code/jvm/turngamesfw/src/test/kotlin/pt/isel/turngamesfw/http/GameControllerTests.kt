package pt.isel.turngamesfw.http

import com.fasterxml.jackson.databind.ObjectMapper
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
import pt.isel.turngamesfw.domain.Game
import pt.isel.turngamesfw.domain.Match
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.http.controller.GameController
import pt.isel.turngamesfw.http.model.Problem
import pt.isel.turngamesfw.http.pipeline.AuthenticationInterceptor
import pt.isel.turngamesfw.http.pipeline.AuthorizationHeaderProcessor
import pt.isel.turngamesfw.http.pipeline.UserArgumentResolver
import pt.isel.turngamesfw.services.GameServices
import pt.isel.turngamesfw.services.results.*
import pt.isel.turngamesfw.utils.Either
import java.time.Instant
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(GameController::class, AuthenticationInterceptor::class)
class GameControllerTests {

    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var gameService: GameServices

    @MockkBean
    private lateinit var authorizationHeaderProcessor: AuthorizationHeaderProcessor

    private val contentTypeSiren = "application/vnd.siren+json"

    private val chessGame = Game("Chess", 2, "Desc", "Rules")
    private val chessMatch = Match(
        gameName = "Chess",
        state = Match.State.ON_GOING,
        players = listOf(1, 2),
        currPlayer = 1,
        currTurn = 1,
        deadlineTurn = Instant.now(),
        info = ObjectMapper().valueToTree(object{})
    )

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(GameController(gameService))
            .addInterceptors(AuthenticationInterceptor(authorizationHeaderProcessor))
            .setCustomArgumentResolvers(UserArgumentResolver())
            .build()

        every { authorizationHeaderProcessor.process(any()) } returns User(
            1,
            "User1",
            User.PasswordValidationInfo("12345"),
        )

        every { gameService.getGameInfo(any()) } returns null
        every { gameService.getGameInfo("Chess") } returns chessGame
    }

    @Test
    fun `getGameInfo returns correct GameInfo`() {
        val body = "{\"class\":[\"game\"],\"properties\":{\"name\":Chess}}"

        mockMvc.perform(get("/game/Chess"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(contentTypeSiren))
            .andExpect(content().json(body))
    }

    @Test
    fun `getGameInfo with invalid game returns error`() {
        val expectedJson = Gson().toJson(Problem.GAME_NOT_EXIST)

        mockMvc.perform(get("/game/NotExist"))
            .andExpect(status().is4xxClientError)
            .andExpect(content().json(expectedJson))
    }

    @Test
    fun `findMatch returns correct with location`() {
        every { gameService.findMatch("Chess", 1) } returns Either.Right(FindMatchSuccess.SearchingMatch)

        mockMvc.perform(post("/game/Chess/find"))
            .andExpect(status().is3xxRedirection)
            .andExpect(header().stringValues("Location", Uris.Game.foundByGameName("Chess").toString()))
    }

    @Test
    fun `findMatch with user already searching returns error`() {
        every { gameService.findMatch("Chess", 1) } returns Either.Left(FindMatchError.AlreadySearchingOrInGame)
        val expectedJson = Gson().toJson(Problem.USER_ALREADY_SEARCHING_IN_GAME)

        mockMvc.perform(post("/game/Chess/find"))
            .andExpect(status().is4xxClientError)
            .andExpect(content().json(expectedJson))
    }

    @Test
    fun `findMatch with invalid game returns error`() {
        every { gameService.findMatch("Chess", 1) } returns Either.Left(FindMatchError.GameNotExist)
        val expectedJson = Gson().toJson(Problem.GAME_NOT_EXIST)

        mockMvc.perform(post("/game/Chess/find"))
            .andExpect(status().is4xxClientError)
            .andExpect(content().json(expectedJson))
    }

    @Test
    fun `foundMatch returns correct match`() {
        every { gameService.foundMatch("Chess", 1) } returns Either.Right(chessMatch)

        val body = "{\"class\":[\"match\"],\"properties\":{\"gameName\":Chess}}"

        mockMvc.perform(get("/game/Chess/found"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(contentTypeSiren))
            .andExpect(content().json(body))
    }

    @Test
    fun `foundMatch with invalid user returns error`() {
        every { gameService.foundMatch("Chess", 1) } returns Either.Left(FoundMatchError.UserNotInGame)
        val expectedJson = Gson().toJson(Problem.USER_NOT_IN_MATCH)

        mockMvc.perform(get("/game/Chess/found"))
            .andExpect(status().is4xxClientError)
            .andExpect(content().json(expectedJson))
    }

    @Test
    fun `foundMatch with no gameLogic returns server error`() {
        every { gameService.foundMatch("Chess", 1) } returns Either.Left(FoundMatchError.ServerError)
        val expectedJson = Gson().toJson(Problem.SERVER_ERROR)

        mockMvc.perform(get("/game/Chess/found"))
            .andExpect(status().is5xxServerError)
            .andExpect(content().json(expectedJson))
    }

    @Test
    fun `getMatchById returns correct match`() {
        every { gameService.getMatchById(chessMatch.id, 1) } returns Either.Right(chessMatch)

        val body = "{\"class\":[\"match\"],\"properties\":{\"gameName\":Chess}}"

        mockMvc.perform(get("/game/Chess/match/${chessMatch.id}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(contentTypeSiren))
            .andExpect(content().json(body))
    }

    @Test
    fun `getMatchById invalid match id returns error`() {
        every { gameService.getMatchById(any(), any()) } returns Either.Left(MatchByIdError.MatchNotExist)
        val expectedJson = Gson().toJson(Problem.MATCH_NOT_EXIST)

        mockMvc.perform(get("/game/Chess/match/${UUID.randomUUID()}"))
            .andExpect(status().is4xxClientError)
            .andExpect(content().json(expectedJson))
    }

    @Test
    fun `getMatchById invalid no gameLogic returns error`() {
        every { gameService.getMatchById(any(), any()) } returns Either.Left(MatchByIdError.ServerError)
        val expectedJson = Gson().toJson(Problem.SERVER_ERROR)

        mockMvc.perform(get("/game/Chess/match/${UUID.randomUUID()}"))
            .andExpect(status().is5xxServerError)
            .andExpect(content().json(expectedJson))
    }

    @Test
    fun `setup returns correct response`() {
        every { gameService.setup(chessMatch.id, any()) } returns Either.Right(SetupMatchSuccess.SetupDone("Setup Done"))

        mockMvc.perform(post("/game/Chess/setup").content("{\"matchId\":\"${chessMatch.id}\", \"info\":{\"id\":\"123\",\"message\":\"Setup\"}}").contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andExpect(content().string("Setup Done"))
    }

    @Test
    fun `setup with invalid match id returns error`() {
        every { gameService.setup(any(), any()) } returns Either.Left(SetupMatchError.MatchNotExist)

        mockMvc.perform(post("/game/Chess/setup").content("{\"matchId\":\"${UUID.randomUUID()}\", \"info\":{\"id\":\"123\",\"message\":\"Setup\"}}").contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun `setup with error on server returns error`() {
        every { gameService.setup(any(), any()) } returns Either.Left(SetupMatchError.ServerError)

        mockMvc.perform(post("/game/Chess/setup").content("{\"matchId\":\"${chessMatch.id}\", \"info\":{\"id\":\"123\",\"message\":\"Setup\"}}").contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is5xxServerError)
    }

    @Test
    fun `setup with user not in match returns error`() {
        every { gameService.setup(any(), any()) } returns Either.Left(SetupMatchError.UserNotInMatch)

        mockMvc.perform(post("/game/Chess/setup").content("{\"matchId\":\"${chessMatch.id}\", \"info\":{\"id\":\"123\",\"message\":\"Setup\"}}").contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is4xxClientError)
    }

}