package pt.isel.tictactow

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import pt.isel.fwinterfaces.Game
import pt.isel.fwinterfaces.GameLogic
import pt.isel.fwinterfaces.Match
import java.lang.IllegalArgumentException

class TicTacTowGameLogic: GameLogic {

    companion object {
        class PositionDeserializer : JsonDeserializer<Position>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Position {
                val node: JsonNode = parser.codec.readTree(parser)
                val col: Int = node.get("col").asInt()
                val row: Int = node.get("row").asInt()
                return Position(col, row)
            }
        }

        class BoardDeserializer : JsonDeserializer<Board>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Board {
                val node: JsonNode = parser.codec.readTree(parser)
                val cellsNode: JsonNode = node.get("cells")
                val cells: Array<Array<Board.State>> = cellsNode.map { rowNode ->
                    rowNode.map { cellNode ->
                        val stateName: String = cellNode.asText()
                        val state: Board.State = Board.State.valueOf(stateName)
                        state
                    }.toTypedArray()
                }.toTypedArray()
                return Board(cells)
            }
        }

        private val modulePosition: SimpleModule = SimpleModule().addDeserializer(Position::class.java, PositionDeserializer())
        private val moduleBoard: SimpleModule = SimpleModule().addDeserializer(Board::class.java, BoardDeserializer())
        private val objectMapper: ObjectMapper = ObjectMapper().registerModule(modulePosition).registerModule(moduleBoard)
    }

    override fun create(users: List<Int>): Match {
        if (users.size != 2) {
            // TODO: Should throw error because list of players does not contain the right number
            throw IllegalArgumentException()
        }
        return Match(
            gameName = "TicTacTow",
            state = Match.State.ON_GOING,
            players = users,
            currPlayer = users.first(),
            currTurn = 1,
            info = objectMapper.valueToTree(Board.create())
        )
    }

    override fun setup(match: Match, infoSetup: GameLogic.InfoSetup): GameLogic.UpdateInfo {
        TODO("Not yet implemented")
    }

    override fun doTurn(match: Match, infoTurn: GameLogic.InfoTurn): GameLogic.UpdateInfo {
        val position = objectMapper.treeToValue(infoTurn.info, Position::class.java)
        return applyRound(match, infoTurn.playerId, position)
    }

    override fun matchPlayerView(match: Match, playerId: Int): Match {
        return match
    }

    override fun getGameInfo(): Game = Game(
        "TicTacTow",
        2,
        "Description",
        "Rules"
    )

    private fun applyRound(
        match: Match,
        playerId: Int,
        position: Position,
    ): GameLogic.UpdateInfo {
        if (!match.players.contains(playerId)) { // No need to exist
            return GameLogic.UpdateInfo(true, "Player not in match", null)
        }
        return when (match.state) { // No need to exist
            Match.State.SETUP -> GameLogic.UpdateInfo(true, "Impossible to be on setup", null)
            Match.State.FINISHED -> GameLogic.UpdateInfo(false, "Game Already Ended", match)
            Match.State.ON_GOING -> {
                if (match.currPlayer != playerId) { // No need to exist
                    return GameLogic.UpdateInfo(true, "Not player Turn", match)
                }
                val playerNext: Int
                val playerState = if (match.currPlayer == match.players.first()) {
                    playerNext = 1
                    Board.State.PLAYER_X
                } else {
                    playerNext = 0
                    Board.State.PLAYER_O
                }
                val board: Board = objectMapper.treeToValue(match.info, Board::class.java)
                if (!board.canPlayOn(position)) {
                    return GameLogic.UpdateInfo(true, "Position not available!", null)
                }

                val newBoard = board.mutate(position, playerState)
                if (newBoard.hasWon(playerState)) {
                    val newMatch = match.copy(info = objectMapper.valueToTree(newBoard), state = Match.State.FINISHED)
                    return GameLogic.UpdateInfo(false, "Player ${playerState.char} won! Game Ended.", newMatch)
                }

                if (newBoard.isFull()) {
                    val newMatch = match.copy(
                        info = objectMapper.valueToTree(newBoard),
                        state = Match.State.FINISHED,
                    )
                    return GameLogic.UpdateInfo(
                        false,
                        "Draw! Game Ended.",
                        newMatch
                    )
                } else {
                    val newMatch = match.copy(
                        info = objectMapper.valueToTree(newBoard),
                        currPlayer = match.players[playerNext],
                        currTurn = match.currTurn + 1,
                    )
                    return GameLogic.UpdateInfo(
                        false,
                        "Next Player.",
                        newMatch
                    )
                }
            }
        }
    }
}
