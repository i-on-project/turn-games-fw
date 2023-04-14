package pt.isel.turngamesfw.repository

import pt.isel.turngamesfw.domain.Game
import pt.isel.turngamesfw.domain.LeaderboardUser
import pt.isel.turngamesfw.domain.Match
import pt.isel.turngamesfw.domain.User
import java.util.UUID

interface GameRepository {

    fun createGame(game: Game)

    fun getGame(name: String): Game?

    fun getGameLeaderBoard(gameName: String, page: Int, limit: Int): List<LeaderboardUser>

    fun updateRating(userId: Int, gameName: String, rating: Int)

    fun getUserState(userId: Int, nameGame: String): User.Stats.State

    fun updateState(userId: Int, nameGame: String, state: User.Stats.State)

    fun getPlayersSearching(nameGame: String): List<User>

    fun createMatch(match: Match)

    fun getMatchById(id: UUID): Match?

    fun getAllGameMatchesByUser(nameGame: String, userId: Int): List<Match>

    fun updateMatch(match: Match)

}