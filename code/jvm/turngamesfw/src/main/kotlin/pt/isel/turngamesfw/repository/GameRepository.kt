package pt.isel.turngamesfw.repository

import pt.isel.turngamesfw.domain.Game
import pt.isel.turngamesfw.domain.Match
import pt.isel.turngamesfw.domain.User
import java.util.UUID

interface GameRepository {

    fun createGame(game: Game)

    fun getGame(name: String): Game

    fun updateStatus(nameGame: String, status: User.Stats.Status)

    fun getPlayersSearching(name: String): List<User>

    fun createMatch(match: Match)

    fun getMatchById(id: UUID): Match

    fun updateMatch(id: UUID): Match

}