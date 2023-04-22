package pt.isel.turngamesfw.services

import org.springframework.stereotype.Component
import pt.isel.turngamesfw.domain.*
import pt.isel.turngamesfw.repository.TransactionManager
import pt.isel.turngamesfw.services.results.*
import pt.isel.turngamesfw.utils.Either
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
    
    fun findMatch(gameName: String, userId: Int): FindMatchResult {
        return transactionManager.run {
            val game = it.gamesRepository.getGame(gameName) ?: return@run Either.Left(FindMatchError.GameNotExist)
            val userState = it.gamesRepository.getUserState(userId, gameName)
            if (userState != User.Stats.State.INACTIVE) {
                return@run Either.Left(FindMatchError.AlreadySearchingOrInGame)
            }

            it.gamesRepository.updateState(userId, gameName, User.Stats.State.SEARCHING)
            tryPairPlayers(gameName, game.numPlayers)
            return@run Either.Right(FindMatchSuccess.SearchingMatch)
        }
    }

    private fun tryPairPlayers(gameName: String, numPlayers: Int) {
        transactionManager.run {
            val searchingPlayers = it.gamesRepository.getPlayersSearching(gameName)

            if (searchingPlayers.size < numPlayers) {
                return@run
            }

            val inGamePlayers = searchingPlayers.take(numPlayers).map{ user -> user.id}
            val gameLogic = gameProvider.getGameLogic(gameName) ?: return@run // TODO: "Return server error gameLogic not found"
            val match = gameLogic.create(inGamePlayers) // TODO: Maybe gameLogic create should only create whats important

            inGamePlayers.forEach { userId ->
                it.gamesRepository.updateState(userId, gameName, User.Stats.State.IN_GAME)
            }

            it.gamesRepository.createMatch(match)

        }
    }

    fun foundMatch(gameName: String, userId: Int): FoundMatchResult {
        return transactionManager.run {
            val userState = it.gamesRepository.getUserState(userId, gameName)
            if (userState != User.Stats.State.IN_GAME) {
                return@run Either.Left(FoundMatchError.UserNotFound)
            }

            val matches = it.gamesRepository.getAllGameMatchesByUser(gameName, userId)

            val match = matches.firstOrNull<Match> { match -> match.state != Match.State.FINISHED } ?: return@run Either.Left(FoundMatchError.ServerError)

            return@run Either.Right(match)
        }
    }

    fun getMatchById(matchId: UUID, userId: Int): MatchByIdResult {
        return transactionManager.run {
            val match = it.gamesRepository.getMatchById(matchId) ?: return@run Either.Left(MatchByIdError.MatchNotExist)
            val gameLogic = gameProvider.getGameLogic(match.gameName) ?: return@run Either.Left(MatchByIdError.ServerError)
            return@run Either.Right(gameLogic.matchPlayerView(match, userId))
        }
    }

    fun setup(matchId: UUID, infoSetup: GameLogic.InfoSetup): SetupMatchResult {
        return transactionManager.run {
            val match = it.gamesRepository.getMatchById(matchId) ?: return@run Either.Left(SetupMatchError.MatchNotExist)
            if (!match.players.contains(infoSetup.playerId)) {
                return@run Either.Left(SetupMatchError.UserNotInMatch)
            }

            if (match.state != Match.State.SETUP) {
                return@run Either.Left(SetupMatchError.MatchStateError)
            }

            val gameLogic = gameProvider.getGameLogic(match.gameName) ?: return@run Either.Left(SetupMatchError.ServerError)
            val updateInfo = gameLogic.setup(match, infoSetup)

            if (!updateInfo.error) {
                if (updateInfo.match == null) {
                    return@run Either.Left(SetupMatchError.ServerError)
                }
                it.gamesRepository.updateMatch(updateInfo.match)
                return@run Either.Right(SetupMatchSuccess.SetupDone(updateInfo.message))
            } else {
                return@run Either.Right(SetupMatchSuccess.ErrorInGameLogic(updateInfo.message))
            }
        }
    }

    fun doTurn(matchId: UUID, infoTurn: GameLogic.InfoTurn): DoTurnMatchResult {
        return transactionManager.run {
            val match = it.gamesRepository.getMatchById(matchId) ?: return@run Either.Left(DoTurnMatchError.MatchNotExist)
            if (!match.players.contains(infoTurn.playerId)) {
                return@run Either.Left(DoTurnMatchError.UserNotInMatch)
            }

            if (match.state != Match.State.ON_GOING) {
                return@run Either.Left(DoTurnMatchError.MatchStateError)
            }

            if (match.currPlayer != infoTurn.playerId) {
                return@run Either.Left(DoTurnMatchError.NotYourTurn)
            }

            val gameLogic = gameProvider.getGameLogic(match.gameName) ?: return@run Either.Left(DoTurnMatchError.ServerError)
            val updateInfo = gameLogic.doTurn(match, infoTurn)

            if (!updateInfo.error) {
                if (updateInfo.match == null) {
                    return@run Either.Left(DoTurnMatchError.ServerError)
                }
                it.gamesRepository.updateMatch(updateInfo.match)
                // TODO: Rating checks
                return@run Either.Right(DoTurnMatchSuccess.DoTurnDone(updateInfo.message))
            } else {
                return@run Either.Right(DoTurnMatchSuccess.ErrorInGameLogic(updateInfo.message))
            }
        }
    }

    fun checkAndSaveAllGames() {
        transactionManager.run {
            gameProvider.getAllGameLogic().forEach { gameLogic ->
                val game = gameLogic.getGameInfo()
                if (it.gamesRepository.getGame(game.name) == null) {
                    it.gamesRepository.createGame(game)
                }
            }
        }
    }

}