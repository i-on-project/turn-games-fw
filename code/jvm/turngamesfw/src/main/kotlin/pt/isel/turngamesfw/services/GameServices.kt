package pt.isel.turngamesfw.services

import org.springframework.stereotype.Component
import pt.isel.turngamesfw.domain.Game
import pt.isel.turngamesfw.domain.GameProvider
import pt.isel.turngamesfw.domain.Match
import pt.isel.turngamesfw.domain.User

@Component
class GameServices(
    private val gameProvider: GameProvider
) {

    fun getGameInfo(gameName: String): Game? {
        TODO()
    }

    fun findMatch(gameName: String, user: User): Boolean {
        tryPairPlayers(gameName)
        TODO()
    }

    /***
     * Asynchronous function:
     * Try to pair players that are searching game, and create match if possible
     */
    private fun tryPairPlayers(gameName: String) {
        TODO("Make it async")
    }

    fun foundMatch(gameName: String, user: User): Match? {
        TODO()
    }

    fun getMatchById(gameName: String, matchId: Int, user: User): Match? {
        TODO()
    }

    fun setup(gameName: String, matchId: Int, infoSetup: Any): Any {
        TODO()
    }

    fun doTurn(gameName: String, matchId: Int, infoTurn: Any): Any {
        TODO()
    }


}