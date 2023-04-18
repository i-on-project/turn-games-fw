package pt.isel.turngamesfw.repository.jdbi

import pt.isel.turngamesfw.domain.User
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.turngamesfw.domain.Game
import pt.isel.turngamesfw.domain.LeaderboardUser
import pt.isel.turngamesfw.domain.Match
import pt.isel.turngamesfw.repository.GameRepository
import java.util.*

class JdbiGameRepository(private val handle: Handle) : GameRepository {
    override fun createGame(game: Game) {
        handle.createUpdate("insert into dbo.Games (name, description, num_players, rules) values (:name, :description, :numPlayers, :rules)")
            .bind("name", game.name)
            .bind("numPlayers", game.numPlayers)
            .bind("description", game.description)
            .bind("rules", game.rules)
            .execute()
    }

    override fun getGame(name: String): Game? {
        return try {
            handle.createQuery("select * from dbo.Games where name = :name")
                .bind("name", name)
                .mapTo<Game>()
                .single()
        } catch (e: Exception) { null }
    }

    override fun getGameLeaderBoard(gameName: String, page: Int, limit: Int): List<LeaderboardUser> {
        data class LU(val username: String, val rating: Int)

        val r = handle.createQuery("""select u.username, us.rating from dbo.UserStats us inner join dbo.Users u 
            on us.user_id = u.id where us.game_name = :gameName order by us.rating desc limit :limit offset :offset""")
            .bind("gameName", gameName)
            .bind("limit", limit)
            .bind("offset", page * limit)
        return r.mapTo<LU>().mapIndexed { i, it -> LeaderboardUser(it.username, it.rating, page * limit + i) }
    }

    override fun createUserStats(userId: Int, gameName: String, initialRating: Int, state: User.Stats.State) {
        handle.createUpdate("insert into dbo.UserStats (user_id, game_name, rating, state) values (:user_id, :game_name, :rating, :state)")
            .bind("user_id", userId)
            .bind("game_name", gameName)
            .bind("rating", initialRating)
            .bind("state", state)
            .execute()
    }

    override fun getUserStats(userId: Int, gameName: String): User.Stats {
        return handle.createQuery("select * from dbo.UserStats where user_id = :user_id and game_name = :game_name")
            .bind("user_id", userId)
            .bind("game_name", gameName)
            .mapTo<User.Stats>()
            .single()
    }

    override fun updateRating(userId: Int, gameName: String, rating: Int) {
        handle.createUpdate("update dbo.UserStats set rating = :rating where user_id = :user_id and game_name = :game_name")
            .bind("rating", rating)
            .bind("user_id", userId)
            .bind("game_name", gameName)
            .execute()
    }

    override fun getUserState(userId: Int, nameGame: String): User.Stats.State {
        return handle.createQuery("select state from dbo.UserStats where user_id = :user_id and game_name = :game_name")
            .bind("user_id", userId)
            .bind("game_name", nameGame)
            .mapTo<User.Stats.State>()
            .single()
    }
    override fun updateState(userId: Int, nameGame: String, state: User.Stats.State) {
        handle.createUpdate("update dbo.UserStats set state = :state where user_id = :user_id and game_name = :game_name")
            .bind("state", state)
            .bind("user_id", userId)
            .bind("game_name", nameGame)
            .execute()
    }

    override fun getPlayersSearching(nameGame: String): List<User> {
        return handle.createQuery("select * from dbo.UserStats us inner join dbo.Users u on us.user_id = u.id where us.game_name = :name and us.state = :state")
            .bind("name", nameGame)
            .bind("state", User.Stats.State.SEARCHING)
            .mapTo<User>()
            .list()
    }

    override fun createMatch(match: Match) {
        handle.createUpdate(
            "insert into dbo.Matches (id, game_name, state, curr_player, curr_turn, deadline_turn, created, info) " +
                    "values (:id, :gameName, :state, :currPlayer, :currTurn, :deadlineTurn, :created, :info)"
        )
            .bind("id", match.id)
            .bind("gameName", match.gameName)
            .bind("state", match.state)
            .bind("currPlayer", match.currPlayer)
            .bind("currTurn", match.currTurn)
            .bind("deadlineTurn", match.deadlineTurn)
            .bind("created", match.created)
            .bind("info", match.info)
            .execute()

        match.players.forEach { playerId ->
            handle.createUpdate("insert into dbo.UserMatches (match_id, user_id) values (:match_id, :user_id)")
                .bind("match_id", match.id)
                .bind("user_id", playerId)
                .execute()
        }
    }

    override fun getMatchById(id: UUID): Match? {
        return handle.createQuery("select * from dbo.Matches where id = :id")
            .bind("id", id)
            .mapTo<Match>()
            .single()
    }

    override fun getAllGameMatchesByUser(nameGame: String, userId: Int): List<Match> {
        return handle.createQuery("select m.* from dbo.Matches m inner join dbo.UserMatches um on m.id = um.match_id where m.gameName = :gameName and um.user_id = :user_id")
            .bind("gameName", nameGame)
            .bind("user_id", userId)
            .mapTo<Match>()
            .list()
    }

    override fun updateMatch(match: Match) {
        handle.createUpdate(
            "update dbo.Matches set state = :state, curr_player = :currPlayer, " +
                    "curr_turn = :currTurn, deadline_turn = :deadlineTurn, info = :info where id = :id"
        )
            .bind("id", match.id)
            .bind("state", match.state)
            .bind("currPlayer", match.currPlayer)
            .bind("currTurn", match.currTurn)
            .bind("deadlineTurn", match.deadlineTurn)
            .bind("info", match.info)
            .execute()
    }

    override fun getUserRatingById(id: Int, gameName: String): Int {
        return handle.createQuery("select rating from dbo.UserStats where user_id = :user_id and game_name = :game_name")
            .bind("user_id", id)
            .bind("game_name", gameName)
            .mapTo<Int>()
            .single()
    }
}