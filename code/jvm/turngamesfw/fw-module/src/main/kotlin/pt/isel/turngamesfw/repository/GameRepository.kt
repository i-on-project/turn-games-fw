package pt.isel.turngamesfw.repository

import pt.isel.fwinterfaces.Game
import pt.isel.turngamesfw.domain.LeaderboardUser
import pt.isel.fwinterfaces.Match
import pt.isel.turngamesfw.domain.User
import java.util.UUID

interface GameRepository {
    fun getAllGameNames(): List<String>

    fun createGame(game: Game)

    fun getGame(name: String): Game?

    fun getGameLeaderBoard(gameName: String, page: Int, limit: Int): List<LeaderboardUser>

    fun createUserStats(userId: Int, gameName: String, initialRating: Int, state: User.Stats.State)

    fun getUserStats(userId: Int, gameName: String): User.Stats

    fun getUserRatingById(id: Int, gameName: String): Int

    fun updateRating(userId: Int, gameName: String, rating: Int)

    fun getUserState(userId: Int, nameGame: String): User.Stats.State

    fun updateState(userId: Int, nameGame: String, state: User.Stats.State)

    fun getPlayersSearching(nameGame: String): List<User>

    fun createMatch(match: Match)

    fun getMatchById(id: UUID): Match?

    fun updateMatch(match: Match)

    fun getAllGameMatchesByUser(nameGame: String, userId: Int): List<Match>
}