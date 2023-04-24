package pt.isel.turngamesfw.repository

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.jdbi.v3.core.Handle
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
    private data class Init(val handle: Handle) {
        val gameRepo = JdbiGameRepository(handle)
        val userRepo = JdbiUserRepository(handle)

        val game = Game("tic-tac-toe", 2, "Get 3 in a row.", "One player at a time.")

        private fun pvi(p: String) = User.PasswordValidationInfo(p)

        val user = User(userRepo.createUser("user", pvi("pass")), "user", pvi("pass"))
        val users = (0..19).map { User(userRepo.createUser("user$it", pvi("pass$it")), "user$it", pvi("pass$it")) }

        init {
            gameRepo.createGame(game)
            gameRepo.createUserStats(user.id, "tic-tac-toe", 0, User.Stats.State.INACTIVE)
            users.forEach { gameRepo.createUserStats(it.id, "tic-tac-toe", it.id * 100, User.Stats.State.INACTIVE) }
        }
    }


    @Test
    fun `create and get game`() {
        testWithHandleAndRollback { handle ->
            val i = Init(handle)

            i.gameRepo.getGame("tic-tac-toe").let { g ->
                assertNotNull(g)
                assertEquals(i.game.name, g.name)
                assertEquals(i.game.numPlayers, g.numPlayers)
                assertEquals(i.game.description, g.description)
                assertEquals(i.game.rules, g.rules)
            }
        }
    }

    @Test
    fun `create and get user stats, then update and get rating and stats`() {
        testWithHandleAndRollback { handle ->
            val i = Init(handle)

            //getUserStats
            i.gameRepo.getUserStats(i.user.id, i.game.name).let { stats ->
                assertNotNull(stats)
                assertEquals(0, stats.rating)
                assertEquals(User.Stats.State.INACTIVE, stats.state)
            }

            //updateRating
            i.gameRepo.updateRating(i.user.id, i.game.name, 100)

            //getUserRatingById
            assertEquals(100, i.gameRepo.getUserRatingById(i.user.id, i.game.name))

            //updateState
            i.gameRepo.updateState(i.user.id, i.game.name, User.Stats.State.SEARCHING)

            //getUserState
            assertEquals(User.Stats.State.SEARCHING, i.gameRepo.getUserState(i.user.id, i.game.name))
        }
    }

    @Test
    fun `get game leaderboard page 0 to 3 with limit 5`() {
        testWithHandleAndRollback { handle ->
            val i = Init(handle)

            //getGameLeaderBoard
            (0..3).forEach { page ->
                i.gameRepo.getGameLeaderBoard(i.game.name, page, 5).let { leaderboard ->
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
            val i = Init(handle)

            i.gameRepo.updateState(i.users[0].id, i.game.name, User.Stats.State.SEARCHING)
            i.gameRepo.updateState(i.users[1].id, i.game.name, User.Stats.State.SEARCHING)

            i.gameRepo.getPlayersSearching(i.game.name).let { players ->
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
            val i = Init(handle)

            //createMatch
            val created = Instant.now().truncatedTo(ChronoUnit.SECONDS)
            val deadLine = created.plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            val matchInfo: JsonNode = ObjectMapper().valueToTree("X__O_____")
            val match = Match(UUID.randomUUID(), i.game.name, Match.State.ON_GOING, i.users.map { it.id }, i.users[0].id, 2, deadLine, created, matchInfo)
            i.gameRepo.createMatch(match)

            //getMatch
            i.gameRepo.getMatchById(match.id).let { m ->
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
            val newMatch = Match(match.id, i.game.name, Match.State.FINISHED, i.users.map { it.id }, i.users[0].id, 5, newDeadLine, created, newMatchInfo)
            i.gameRepo.updateMatch(newMatch)

            //get updated match
            i.gameRepo.getMatchById(match.id).let { m ->
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
            val i = Init(handle)

            val created = Instant.now().truncatedTo(ChronoUnit.SECONDS)
            val deadLine = created.plusSeconds(60).truncatedTo(ChronoUnit.SECONDS)
            val matchInfo: JsonNode = ObjectMapper().valueToTree("X__O_XXO_")

            //create 5 matches
            repeat(5) {
                val match = Match(UUID.randomUUID(), i.game.name, Match.State.ON_GOING, i.users.map { it.id }, i.users[0].id, 2, deadLine, created, matchInfo)
                i.gameRepo.createMatch(match)
            }

            //get all user matches
            i.gameRepo.getAllGameMatchesByUser(i.game.name, i.users[0].id).let { matches ->
                assertEquals(5, matches.size)
                (0..4).forEach { n ->
                    assertEquals(i.game.name, matches[n].gameName)
                    assertEquals(Match.State.ON_GOING, matches[n].state)
                    assertEquals(i.users.map { it.id }, matches[n].players)
                    assertEquals(i.users[0].id, matches[n].currPlayer)
                    assertEquals(2, matches[n].currTurn)
                    assertEquals(deadLine, matches[n].deadlineTurn)
                    assertEquals(matchInfo, matches[n].info)
                }
            }
        }
    }
}