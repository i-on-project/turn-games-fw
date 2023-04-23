package pt.isel.turngamesfw.repository

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import pt.isel.turngamesfw.domain.Game
import pt.isel.turngamesfw.domain.Match
import pt.isel.turngamesfw.domain.User
import pt.isel.turngamesfw.repository.jdbi.JdbiGameRepository
import pt.isel.turngamesfw.repository.jdbi.JdbiUserRepository
import pt.isel.turngamesfw.utils.testWithHandleAndRollback
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameRepositoryTests {
    private val game = Game("tic-tac-toe", 2, "Get 3 in a row.", "One player at a time.")

    @Test
    fun `create and get game`() {
        testWithHandleAndRollback { handle ->
            val gameRepo = JdbiGameRepository(handle)

            //createGame
            gameRepo.createGame(game)

            //getGame
            gameRepo.getGame("tic-tac-toe").let { g ->
                assertNotNull(g)
                assertEquals(game.name, g.name)
                assertEquals(game.numPlayers, g.numPlayers)
                assertEquals(game.description, g.description)
                assertEquals(game.rules, g.rules)
            }
        }
    }
    
    @Test
    fun `create and get user stats, then update and get rating and stats`() {
        testWithHandleAndRollback { handle ->
            val gameRepo = JdbiGameRepository(handle)
            val userRepo = JdbiUserRepository(handle)

            //createGame
            gameRepo.createGame(game)

            //createUser
            fun pvi(p: String) = User.PasswordValidationInfo(p)
            val user = User(userRepo.createUser("user", pvi("pass")), "user", pvi("pass"))
            
            //createUserStats
            gameRepo.createUserStats(user.id, "tic-tac-toe", 0, User.Stats.State.INACTIVE)

            //getUserStats
            gameRepo.getUserStats(user.id, "tic-tac-toe").let { stats ->
                assertNotNull(stats)
                assertEquals(0, stats.rating)
                assertEquals(User.Stats.State.INACTIVE, stats.state)
            }

            //updateRating
            gameRepo.updateRating(user.id, "tic-tac-toe", 100)

            //getUserRatingById
            assertEquals(100, gameRepo.getUserRatingById(user.id, "tic-tac-toe"))

            //updateState
            gameRepo.updateState(user.id, "tic-tac-toe", User.Stats.State.SEARCHING)

            //getUserState
            assertEquals(User.Stats.State.SEARCHING, gameRepo.getUserState(user.id, "tic-tac-toe"))
        }
    }

    @Test
    fun `get game leaderboard page 0 to 3 with limit 5`() {
        testWithHandleAndRollback { handle ->
            val gameRepo = JdbiGameRepository(handle)
            val userRepo = JdbiUserRepository(handle)

            //createGame
            gameRepo.createGame(game)

            //20 samples of users with stats
            fun pvi(p: String) = User.PasswordValidationInfo(p)
            val users = (0..19).map { User(userRepo.createUser("user$it", pvi("pass$it")), "user$it", pvi("pass$it")) }
            users.forEach { gameRepo.createUserStats(it.id, "tic-tac-toe", it.id * 100, User.Stats.State.INACTIVE) }

            //getGameLeaderBoard
            (0..3).forEach { page ->
                gameRepo.getGameLeaderBoard("tic-tac-toe", page, 5).let { leaderboard ->
                    assertEquals(5, leaderboard.size)
                    (0..4).forEach { i -> 
                        assertEquals(page * 5 + i, leaderboard[i].position)
                    }
                }
            }
        }
    }

    @Test
    fun `get players searching`() {
        testWithHandleAndRollback { handle ->
            val gameRepo = JdbiGameRepository(handle)
            val userRepo = JdbiUserRepository(handle)

            //createGame
            gameRepo.createGame(game)

            //2 samples of users with stats
            fun pvi(p: String) = User.PasswordValidationInfo(p)
            val users = (0..1).map { User(userRepo.createUser("user$it", pvi("pass$it")), "user$it", pvi("pass$it")) }
            users.forEach { gameRepo.createUserStats(it.id, "tic-tac-toe", it.id * 100, User.Stats.State.SEARCHING) }

            //getPlayersSearching
            gameRepo.getPlayersSearching("tic-tac-toe").let { players ->
                assertEquals(2, players.size)
                (0..1).forEach { i -> 
                    assertEquals("user$i", players[i].username)
                }
            }
        }
    }

    @Test
    fun `create, get and update match`() {
        testWithHandleAndRollback { handle ->
            val gameRepo = JdbiGameRepository(handle)
            val userRepo = JdbiUserRepository(handle)

            //createGame
            gameRepo.createGame(game)

            //2 samples of users with stats
            fun pvi(p: String) = User.PasswordValidationInfo(p)
            val users = (0..1).map { User(userRepo.createUser("user$it", pvi("pass$it")), "user$it", pvi("pass$it")) }
            users.forEach { gameRepo.createUserStats(it.id, "tic-tac-toe", it.id * 100, User.Stats.State.SEARCHING) }

            //createMatch
            val created = Instant.now().truncatedTo(ChronoUnit.SECONDS)
            val deadLine = created.plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            val matchInfo: JsonNode = ObjectMapper().valueToTree("X__O_____")
            val match = Match(UUID.randomUUID(), "tic-tac-toe", Match.State.ON_GOING, users.map { it.id }, users[0].id, 2, deadLine, created, matchInfo)
            gameRepo.createMatch(match)

            //getMatch
            gameRepo.getMatchById(match.id).let { m ->
                assertNotNull(m)
                assertEquals(match.id, m.id)
                assertEquals(match.gameName, m.gameName)
                assertEquals(Match.State.ON_GOING, m.state)
                assertEquals(match.players, m.players)
                assertEquals(match.currPlayer, m.currPlayer)
                assertEquals(match.currTurn, m.currTurn)
                assertEquals(match.deadlineTurn, m.deadlineTurn)
                assertEquals(match.created, m.created)
                assertEquals(match.info, m.info)
            }

            //updateMatchState
            val newDeadLine = deadLine.plusSeconds(60)
            val newMatchInfo: JsonNode = ObjectMapper().valueToTree("X__OX_O_X")
            val newMatch = Match(match.id, "tic-tac-toe", Match.State.FINISHED, users.map { it.id }, users[0].id, 5, newDeadLine, created, newMatchInfo)
            gameRepo.updateMatch(newMatch)

            //get updated match
            gameRepo.getMatchById(match.id).let { m ->
                assertNotNull(m)
                assertEquals(Match.State.FINISHED, m.state)
                assertEquals(newMatch.currPlayer, m.currPlayer)
                assertEquals(newMatch.currTurn, m.currTurn)
                assertEquals(newDeadLine, m.deadlineTurn)
                assertEquals(newMatchInfo, m.info)
            }            
        }
    }

    @Test
    fun `get all user matches`() {
        testWithHandleAndRollback { handle ->
            val gameRepo = JdbiGameRepository(handle)
            val userRepo = JdbiUserRepository(handle)

            //createGame
            gameRepo.createGame(game)

            //2 samples of users with stats
            fun pvi(p: String) = User.PasswordValidationInfo(p)
            val users = (0..1).map { User(userRepo.createUser("user$it", pvi("pass$it")), "user$it", pvi("pass$it")) }
            users.forEach { gameRepo.createUserStats(it.id, "tic-tac-toe", it.id * 100, User.Stats.State.SEARCHING) }

            val created = Instant.now().truncatedTo(ChronoUnit.SECONDS)
            val deadLine = created.plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            val matchInfo: JsonNode = ObjectMapper().valueToTree("X__O_XXO_")

            //create 5 matches
            repeat(5) {
                val match = Match(UUID.randomUUID(), "tic-tac-toe", Match.State.ON_GOING, users.map { it.id }, users[0].id, 2, deadLine, created, matchInfo)
                gameRepo.createMatch(match)
            }

            //get all user matches
            gameRepo.getAllGameMatchesByUser("tic-tac-toe", users[0].id).let { matches ->
                assertEquals(5, matches.size)
                (0..4).forEach { i -> 
                    assertEquals("tic-tac-toe", matches[i].gameName)
                    assertEquals(Match.State.ON_GOING, matches[i].state)
                    assertEquals(users.map { it.id }, matches[i].players)
                    assertEquals(users[0].id, matches[i].currPlayer)
                    assertEquals(2, matches[i].currTurn)
                    assertEquals(deadLine, matches[i].deadlineTurn)
                    assertEquals(matchInfo, matches[i].info)
                }
            }
        }
    }
}