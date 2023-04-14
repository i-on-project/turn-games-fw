package pt.isel.turngamesfw.services

import org.springframework.stereotype.Component
import pt.isel.turngamesfw.domain.*
import pt.isel.turngamesfw.repository.TransactionManager
import java.util.UUID

@Component
class GameServices(
    private val gameProvider: GameProvider,
    private val transactionManager: TransactionManager
) {

    fun getGameInfo(gameName: String): Game? {
        return transactionManager.run {
            return@run it.gamesRepository.getGame(gameName)
        }
    }
    fun findMatch(gameName: String, userId: Int): Boolean {
        return transactionManager.run {
            val game = it.gamesRepository.getGame(gameName) ?: return@run TODO("Error Game not exist")
            val userState = it.gamesRepository.getUserState(userId, gameName)
            if (userState != User.Stats.State.INACTIVE) {
                return@run TODO("Error user already searching or in-game")
            }

            it.gamesRepository.updateState(userId, gameName, User.Stats.State.SEARCHING)
            tryPairPlayers(gameName, game.numPlayers)
            return@run true
        }
    }

    /***
     * Asynchronous function:
     * Try to pair players that are searching game, and create match if possible
     */
    private fun tryPairPlayers(gameName: String, numPlayers: Int) {
        transactionManager.run {
            val searchingPlayers = it.gamesRepository.getPlayersSearching(gameName)

            if (searchingPlayers.size < numPlayers) {
                return@run
            }

            val inGamePlayers = searchingPlayers.take(numPlayers).map{ user -> user.id}
            val gameLogic = gameProvider.getGameLogic(gameName) ?: return@run // TODO: "Return server error gameLogic not found"
            val match = gameLogic.create(inGamePlayers)

            inGamePlayers.forEach { userId ->
                it.gamesRepository.updateState(userId, gameName, User.Stats.State.IN_GAME)
            }

            it.gamesRepository.createMatch(match)

        }
    }

    fun foundMatch(gameName: String, userId: Int): Match? {
        return transactionManager.run {
            val userState = it.gamesRepository.getUserState(userId, gameName)
            if (userState != User.Stats.State.IN_GAME) {
                return@run TODO("Error user not found match")
            }

            val matches = it.gamesRepository.getAllGameMatchesByUser(gameName, userId)

            return@run matches.firstOrNull<Match> { match -> match.state != Match.State.END }
                ?: return@run TODO("Error server, should have match in database not ended")
        }
    }

    fun getMatchById(matchId: UUID, userId: Int): Match? {
        return transactionManager.run {
            val match = it.gamesRepository.getMatchById(matchId) ?: return@run null
            val gameLogic = gameProvider.getGameLogic(match.gameName) ?: TODO("Return server error gameLogic not found")
            return@run gameLogic.matchPlayerView(match, userId)
        }
    }

    fun setup(matchId: UUID, infoSetup: GameLogic.InfoSetup): Any {
        return transactionManager.run {
            val match = it.gamesRepository.getMatchById(matchId) ?: TODO("Return error match not found")
            if (!match.players.contains(infoSetup.playerId)) {
                return@run TODO("Maybe can return error 'User not in match'")
            }

            val gameLogic = gameProvider.getGameLogic(match.gameName) ?: TODO("Return server error gameLogic not found")
            val updateInfo = gameLogic.setup(match, infoSetup)

            if (!updateInfo.error) {
                if (updateInfo.match == null) {
                    return@run TODO("Return server error gameLogic match is missing")
                }
                it.gamesRepository.updateMatch(updateInfo.match)
            }

            return@run updateInfo.message
        }
    }

    fun doTurn(matchId: UUID, infoTurn: GameLogic.InfoTurn): Any {
        return transactionManager.run {
            val match = it.gamesRepository.getMatchById(matchId) ?: TODO("Return error match not found")
            if (!match.players.contains(infoTurn.playerId)) {
                return@run TODO("Maybe can return error 'User not in match'")
            }

            val gameLogic = gameProvider.getGameLogic(match.gameName) ?: TODO("Return server error gameLogic not found")
            val updateInfo = gameLogic.doTurn(match, infoTurn)

            if (!updateInfo.error) {
                if (updateInfo.match == null) {
                    return@run TODO("Return server error gameLogic match is missing")
                }
                it.gamesRepository.updateMatch(updateInfo.match)
            }

            return@run updateInfo.message
        }
    }

}