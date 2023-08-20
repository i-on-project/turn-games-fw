package pt.isel.turngamesfw.http.model

import com.fasterxml.jackson.databind.JsonNode
import pt.isel.fwinterfaces.Game
import pt.isel.fwinterfaces.Match
import pt.isel.turngamesfw.domain.User
import java.time.Instant
import java.util.UUID

data class SetupInputModel(
    val matchId: UUID,
    val info: JsonNode,
)

data class TurnInputModel(
    val matchId: UUID,
    val info: JsonNode,
)

data class LeaderBoardInputModel(
    val gameName: String,
    val limit: Int,
    val page: Int,
)

data class GameListOutputModel(
    val gameList: List<String>
)

data class GameOutputModel(
    val name: String,
    val numPlayers: Int,
    val description: String,
    val rules: String,
) {
    companion object {
        fun fromGame(game: Game): GameOutputModel =
            GameOutputModel(game.name, game.numPlayers, game.description, game.rules)

    }
}

data class MyStateOutputModel(
    val state: User.Stats.State
)

data class FoundMatchOutputModel(
    val found: Boolean,
    val game: MatchOutputModel?,
)

data class MatchOutputModel(
    val id: UUID,
    val gameName: String,
    val state: Match.State,
    val players: List<Int>,
    val currPlayer: Int,
    val currTurn: Int,
    val deadlineTurn: Instant?,
    val created: Instant = Instant.now(),
    val info: Any,
) {
    companion object {
        fun fromMatch(match: Match) =
            MatchOutputModel(match.id, match.gameName, match.state, match.players, match.currPlayer, match.currTurn, match.deadlineTurn, match.created, match.info)
    }
}

data class MyTurnOutputModel(
    val gameOver: Boolean,
    val setup: Boolean,
    val myTurn: Boolean?,
)