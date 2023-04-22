package pt.isel.turngamesfw.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import pt.isel.turngamesfw.domain.*
import pt.isel.turngamesfw.repository.GameRepository
import pt.isel.turngamesfw.repository.TransactionManager
import pt.isel.turngamesfw.repository.UserRepository
import pt.isel.turngamesfw.services.results.*
import pt.isel.turngamesfw.utils.Either
import pt.isel.turngamesfw.utils.getTransactionManager
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameServicesTests {

    private var gameProvider: GameProvider = GameProvider()

    @MockK
    private lateinit var chessGameLogic: GameLogic

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var gameRepository: GameRepository

    private lateinit var transactionManager: TransactionManager

    private lateinit var gameServices: GameServices

    private val chessGame = Game("Chess", 2, "Desc", "Rules")

    private val chessMatch = Match(
        gameName = "Chess",
        state = Match.State.ON_GOING,
        players = listOf(1, 2),
        currPlayer = 1,
        currTurn = 1,
        deadlineTurn = Instant.now(),
        info = object{}
    )

    private val chessMatchSetup = Match(
        gameName = "Chess",
        state = Match.State.SETUP,
        players = listOf(1, 2),
        currPlayer = 1,
        currTurn = 1,
        deadlineTurn = Instant.now(),
        info = object{}
    )

    private val info: JsonNode = JsonNodeFactory.instance.objectNode()

    @BeforeAll
    fun setUp() {
        MockKAnnotations.init(this)
        gameProvider.addGame("Chess", chessGameLogic)
        every { chessGameLogic.matchPlayerView(any(), any()) } returnsArgument 0
        every { chessGameLogic.setup(chessMatchSetup, any()) } returns GameLogic.UpdateInfo(
            error = false,
            message = "Works",
            match = chessMatchSetup
        )
        every { chessGameLogic.doTurn(chessMatch, any()) } returns GameLogic.UpdateInfo(
            error = false,
            message = "Works",
            match = chessMatch
        )
        transactionManager = getTransactionManager(userRepository, gameRepository)
        every { gameRepository.getGame(any()) } returns null
        every { gameRepository.getGame(chessGame.name) } returns chessGame
        every { gameRepository.getMatchById(any()) } returns null
        every { gameRepository.getMatchById(chessMatch.id) } returns chessMatch
        gameServices = GameServices(gameProvider, transactionManager)
    }

    @Test
    fun `getGameInfo returns correct game`() {
        val gameInfo = gameServices.getGameInfo(chessGame.name)

        assertNotNull(gameInfo, "Game Info should not be null")
        assertEquals("Chess", gameInfo.name, "Game name not correct")
        assertEquals(2, gameInfo.numPlayers, "Game numPlayers not correct")
        assertEquals(chessGame, gameInfo)
    }

    @Test
    fun `getGameInfo with invalid name returns null`() {
        val gameInfo = gameServices.getGameInfo("ABC")

        assertNull(gameInfo, "Game Info should be null")
    }

    @Test
    fun `getMatchById returns correct match`() {
        val match = when (val res = gameServices.getMatchById(chessMatch.id, 1)) {
            is Either.Left -> fail("Should not return error")
            is Either.Right -> res.value
        }

        assertNotNull(match, "Match should not be null")
        assertEquals(chessMatch.id, match.id)
        assertEquals(chessMatch, match)
    }

    @Test
    fun `getMatchById with invalid name returns error`() {
        when (val res = gameServices.getMatchById(UUID.randomUUID(), 1)) {
            is Either.Left -> assertEquals(MatchByIdError.MatchNotExist::class.java, res.value::class.java)
            is Either.Right -> fail("Match should not be found")
        }
    }

    @Test
    fun `findMatch returns true`() {
        val playerId = 1
        var playerStatus = User.Stats.State.INACTIVE
        every { gameRepository.getUserState(playerId, chessGame.name) } returns User.Stats.State.INACTIVE
        every { gameRepository.updateState(playerId, chessGame.name, User.Stats.State.SEARCHING) } answers  { playerStatus = User.Stats.State.SEARCHING }
        every { gameRepository.getPlayersSearching(any()) } returns listOf()

        assertEquals(User.Stats.State.INACTIVE, playerStatus, "Should start INACTIVE")

        when (gameServices.findMatch(chessGame.name, playerId)) {
            is Either.Left -> fail("Should not return error")
            else -> {}
        }

        assertEquals(User.Stats.State.SEARCHING, playerStatus, "Should be SEARCHING")
    }

    @Test
    fun `findMatch with invalid game returns error`() {
        val playerId = 1

        when (val res = gameServices.findMatch("NotExist", playerId)) {
            is Either.Left -> assertEquals(FindMatchError.GameNotExist::class.java, res.value::class.java)
            is Either.Right -> fail("Should not return success")
        }
    }

    @Test
    fun `findMatch with player already searching returns error`() {
        val playerId = 1
        every { gameRepository.getUserState(playerId, chessGame.name) } returns User.Stats.State.SEARCHING

        when (val res = gameServices.findMatch(chessGame.name, playerId)) {
            is Either.Left -> assertEquals(FindMatchError.AlreadySearchingOrInGame::class.java, res.value::class.java)
            is Either.Right -> fail("Should not return success")
        }
    }

    @Test
    fun `foundMatch return correct match`() {
        val playerId = 1
        every { gameRepository.getUserState(playerId, chessGame.name) } returns User.Stats.State.IN_GAME
        every { gameRepository.getAllGameMatchesByUser(chessGame.name, playerId) } returns listOf(chessMatch)

        val match = when( val res = gameServices.foundMatch(chessGame.name, playerId)) {
            is Either.Left -> fail("Should not return error")
            is Either.Right -> res.value
        }

        assertNotNull(match)
        assertEquals(chessMatch.id, match.id)
        assertEquals(chessMatch, match)
    }

    @Test
    fun `foundMatch with invalid player return error`() {
        val playerId = 1
        every { gameRepository.getUserState(playerId, chessGame.name) } returns User.Stats.State.INACTIVE

        when( val res = gameServices.foundMatch(chessGame.name, playerId)) {
            is Either.Left -> assertEquals(FoundMatchError.UserNotFound::class.java, res.value::class.java)
            is Either.Right -> fail("Should not return success")
        }
    }

    @Test
    fun `foundMatch with no game in database return error`() {
        val playerId = 1
        every { gameRepository.getUserState(playerId, chessGame.name) } returns User.Stats.State.IN_GAME
        every { gameRepository.getAllGameMatchesByUser(chessGame.name, playerId) } returns listOf()

        when( val res = gameServices.foundMatch(chessGame.name, playerId)) {
            is Either.Left -> assertEquals(FoundMatchError.ServerError::class.java, res.value::class.java)
            is Either.Right -> fail("Should not return success")
        }
    }

    @Test
    fun `setup returns valid`() {
        val playerId = 1
        var updatedMatch: Match? = null
        val slot = slot<Match>()
        every { gameRepository.updateMatch(capture(slot)) } answers { updatedMatch = slot.captured }
        every { gameRepository.getMatchById(chessMatchSetup.id) } returns chessMatchSetup

        val resp = when (val res = gameServices.setup(chessMatchSetup.id, GameLogic.InfoSetup(playerId, info))) {
            is Either.Left -> fail("Should not return error")
            is Either.Right -> when (val r = res.value) {
                is SetupMatchSuccess.SetupDone -> r.resp
                else -> fail("Should not return other")
            }
        } as String

        assertEquals("Works", resp)
        assertNotNull(updatedMatch)
    }

    @Test
    fun `setup with invalid match returns error`() {
        val playerId = 1

        when (val res = gameServices.setup(UUID.randomUUID(), GameLogic.InfoSetup(playerId, info))) {
            is Either.Left -> assertEquals(SetupMatchError.MatchNotExist::class.java, res.value::class.java)
            is Either.Right -> fail("Should not return success")
        }

    }

    @Test
    fun `setup with invalid user returns error`() {
        val playerId = 3

        when (val res = gameServices.setup(chessMatch.id, GameLogic.InfoSetup(playerId, info))) {
            is Either.Left -> assertEquals(SetupMatchError.UserNotInMatch::class.java, res.value::class.java)
            is Either.Right -> fail("Should not return success")
        }

    }

    @Test
    fun `doTurn returns valid`() {
        val playerId = 1
        var updatedMatch: Match? = null
        val slot = slot<Match>()
        every { gameRepository.updateMatch(capture(slot)) } answers { updatedMatch = slot.captured }

        val resp = when (val res = gameServices.doTurn(chessMatch.id, GameLogic.InfoTurn(playerId, info))) {
            is Either.Left -> fail("Should not return error")
            is Either.Right -> when (val r = res.value) {
                is DoTurnMatchSuccess.DoTurnDone -> r.resp
                else -> fail("Should not return other")
            }
        } as String

        assertEquals("Works", resp)
        assertNotNull(updatedMatch)
    }

    @Test
    fun `doTurn with invalid match returns error`() {
        val playerId = 1

        when (val res = gameServices.doTurn(UUID.randomUUID(), GameLogic.InfoTurn(playerId, info))) {
            is Either.Left -> assertEquals(DoTurnMatchError.MatchNotExist::class.java, res.value::class.java)
            is Either.Right -> fail("Should not return success")
        }

    }

    @Test
    fun `doTurn with invalid user returns error`() {
        val playerId = 3

        when (val res = gameServices.doTurn(chessMatch.id, GameLogic.InfoTurn(playerId, info))) {
            is Either.Left -> assertEquals(DoTurnMatchError.UserNotInMatch::class.java, res.value::class.java)
            is Either.Right -> fail("Should not return success")
        }

    }

}