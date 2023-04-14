package pt.isel.turngamesfw.repository

import org.junit.jupiter.api.Test
import pt.isel.turngamesfw.domain.Game
import pt.isel.turngamesfw.domain.Match
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.repository.jdbi.JdbiGameRepository
import pt.isel.turngamesfw.repository.jdbi.JdbiUserRepository
import pt.isel.turngamesfw.utils.testWithHandleAndRollback
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GameRepositoryTests {
    @Test
    fun `all tests`() = 
        testWithHandleAndRollback { handle ->
            val gameRepo = JdbiGameRepository(handle)
            val userRepo = JdbiUserRepository(handle)

            //createGame
            gameRepo.createGame(Game( "game-name", 5, "game-desc", "game-rules"))
            
            //getGame
            gameRepo.getGame("game-name").let { game ->
                assertNotNull(game)
                assertEquals("game-name", game.name)
                assertEquals(5, game.numPlayers)
                assertEquals("game-desc", game.description)
                assertEquals("game-rules", game.rules)
            }

            //getGame Fail
            gameRepo.getGame("not-found").let { gameNotFound ->
                assertEquals(null, gameNotFound)
            }
            
            //10 samples of users
            val users = (0..9).map { User(it, "user-$it", User.PasswordValidationInfo("pass-$it"), User.Status.ONLINE) }.toList()
            users.forEach { userRepo.createUser(it.username, it.passwordValidation) }
            
            //updateRating (verified in the getGameLeaderBoard)
            users.forEach { userRepo.updateRating(it.id, "game-name", it.id * 100) }

            //getGameLeaderBoard
            gameRepo.getGameLeaderBoard("game-name", 0, 10).let { leaderboard ->
                assertEquals(10, leaderboard.size)
                (0..9).forEach { i -> 
                    assertEquals(i, leaderboard[i].position) 
                    assertEquals(i * 100, leaderboard[i].rating)
                }
            }

            //getState and updateState
            gameRepo.updateState(0, "game-name", User.Stats.State.SEARCHING)
            gameRepo.getState(0, "game-name").let { state ->
                assertEquals(User.Stats.State.SEARCHING, state)
            }

            //getPlayersSearching
            gameRepo.getPlayersSearching("game-name").let { playersSearching ->
                assertEquals(1, playersSearching.size)
                assertEquals(0, playersSearching[0].id)
            }

            //createMatch
            val matchId = UUID.randomUUID()
            var created = Instant.now()
            var deadLine = Instant.now()
            gameRepo.createMatch(Match(matchId, "game-name", Match.State.ON_GOING,
                listOf(users[0].id, users[2].id, users[3].id, users[4].id), 0, 1, deadLine, created, {}))
            
            //getMatchById
            gameRepo.getMatchById(matchId).let { match ->
                assertNotNull(match)
                assertEquals(matchId, match.id)
                assertEquals("game-name", match.gameName)
                assertEquals(Match.State.ON_GOING, match.state)
                assertEquals(listOf(users[0].id, users[2].id, users[3].id, users[4].id), match.players)
                assertEquals(0, match.currPlayer)
                assertEquals(1, match.currTurn)
                assertEquals(deadLine, match.deadlineTurn)
                assertEquals(created, match.created)
                assertEquals({}, match.info)
            }

            created = Instant.now()
            deadLine = Instant.now()
            //updateMatch
            gameRepo.updateMatch(Match(matchId, "game-name", Match.State.END,
                listOf(users[0].id, users[2].id, users[3].id, users[4].id), 5, 12, deadLine, created, {}))
            gameRepo.getMatchById(matchId).let { match ->
                assertNotNull(match)
                assertEquals(matchId, match.id)
                assertEquals("game-name", match.gameName)
                assertEquals(Match.State.END, match.state)
                assertEquals(listOf(users[0].id, users[2].id, users[3].id, users[4].id), match.players)
                assertEquals(5, match.currPlayer)
                assertEquals(12, match.currTurn)
                assertEquals(deadLine, match.deadlineTurn)
                assertEquals(created, match.created)
                assertEquals({}, match.info)
        }
    }
}