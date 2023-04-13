package pt.isel.turngamesfw.services

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
import pt.isel.turngamesfw.utils.getTransactionManager
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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

    @BeforeAll
    fun setUp() {
        MockKAnnotations.init(this)
        gameProvider.addGame("Chess", chessGameLogic)
        every { chessGameLogic.matchPlayerView(any(), any()) } returnsArgument 0
        every { chessGameLogic.setup(chessMatch, any()) } returns GameLogic.UpdateInfo(
            error = false,
            message = "Works",
            match = chessMatch
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
        val match = gameServices.getMatchById(chessMatch.id, 1)

        assertNotNull(match, "Match should not be null")
        assertEquals(chessMatch.id, match.id)
        assertEquals(chessMatch, match)
    }

    @Test
    fun `getMatchById with invalid name returns null`() {
        val match = gameServices.getMatchById(UUID.randomUUID(), 1)

        assertNull(match, "Match should be null")
    }

    @Test
    fun `findMatch returns true`() {
        val playerId = 1
        var playerStatus = User.Stats.State.INACTIVE
        every { gameRepository.getUserState(playerId, chessGame.name) } returns User.Stats.State.INACTIVE
        every { gameRepository.updateState(playerId, chessGame.name, User.Stats.State.SEARCHING) } answers  { playerStatus = User.Stats.State.SEARCHING }
        every { gameRepository.getPlayersSearching(any()) } returns listOf()

        assertEquals(User.Stats.State.INACTIVE, playerStatus, "Should start INACTIVE")

        val resp = gameServices.findMatch(chessGame.name, playerId)

        assertEquals(true, resp)
        assertEquals(User.Stats.State.SEARCHING, playerStatus, "Should be SEARCHING")
    }

    @Test
    fun `foundMatch return correct match`() {
        val playerId = 1
        every { gameRepository.getUserState(playerId, chessGame.name) } returns User.Stats.State.IN_GAME
        every { gameRepository.getAllGameMatchesByUser(chessGame.name, playerId) } returns listOf(chessMatch)

        val match = gameServices.foundMatch(chessGame.name, playerId)

        assertNotNull(match)
        assertEquals(chessMatch.id, match.id)
        assertEquals(chessMatch, match)
    }

    @Test
    fun `setup returns valid`() {
        val playerId = 1
        var updatedMatch: Match? = null
        val slot = slot<Match>()
        every { gameRepository.updateMatch(capture(slot)) } answers { updatedMatch = slot.captured }

        val resp = gameServices.setup(chessMatch.id, GameLogic.InfoSetup(playerId, object{})) as String

        assertEquals("Works", resp)
        assertNotNull(updatedMatch)
    }

    @Test
    fun `doTurn returns valid`() {
        val playerId = 1
        var updatedMatch: Match? = null
        val slot = slot<Match>()
        every { gameRepository.updateMatch(capture(slot)) } answers { updatedMatch = slot.captured }

        val resp = gameServices.doTurn(chessMatch.id, GameLogic.InfoTurn(playerId, object{})) as String

        assertEquals("Works", resp)
        assertNotNull(updatedMatch)
    }


}