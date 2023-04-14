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
        handle.createUpdate("insert into dbo.Games (name, description, numPlayers, rules) values (:name, :description, :numPlayers, :rules)")
            .bind("name", game.name)
            .bind("numPlayers", game.numPlayers)
            .bind("description", game.description)
            .bind("rules", game.rules)
            .execute()
    }

    override fun getGame(name: String): Game? {
        return handle.createQuery("select * from dbo.Games where name = :name")
            .bind("name", name)
            .mapTo<Game>()
            .single()
    }

    override fun getGameLeaderBoard(gameName: String, page: Int, limit: Int): List<LeaderboardUser> {
        return handle.createQuery("""select u.username, u.rating from dbo.UserStats us inner join dbo.Users u 
            on us.user_id = u.id where us.game_name = :gameName order by u.rating desc limit :limit, offset :offset""")
            .bind("gameName", gameName)
            .bind("limit", limit)
            .bind("offset", (page-1) * limit)
            .mapTo<LeaderboardUser>()
            .list()
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
        return handle.createQuery("select * from dbo.UserStats where game_name = :name and state = :state")
            .bind("name", nameGame)
            .bind("state", User.Stats.State.SEARCHING)
            .mapTo<User>()
            .list()
    }

    override fun createMatch(match: Match) {
        handle.createUpdate(
            "insert into dbo.Matches (id, gameName, state, curr_player, curr_turn, deadline_turn, created, info) " +
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
        TODO("Not yet implemented")
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
}