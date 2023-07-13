package pt.isel

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import pt.isel.fwinterfaces.*
import pt.isel.domain.*
/*
class CastleRunGameLogic: GameLogic {
    companion object {
        class CoordsDeserializer : JsonDeserializer<Coords>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Coords {
                val node: JsonNode = parser.codec.readTree(parser)
                val row: Int = node.get("row").asInt()
                val col: Int = node.get("col").asInt()
                return Coords(row, col)
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
                return Board(playerA, playerB, numRows, numCols, numPieces)
            }
        }

        private val modulePosition: SimpleModule =
            SimpleModule().addDeserializer(Coords::class.java, CoordsDeserializer())
        private val moduleBoard: SimpleModule = SimpleModule().addDeserializer(Board::class.java, BoardDeserializer())
        private val objectMapper: ObjectMapper =
            ObjectMapper().registerModule(modulePosition).registerModule(moduleBoard)
    }

    override fun getGameInfo() = Game(
        "CastleRun",
        2,
        "Castle Run is mostly a luck based game, but because the player can take different actions during the round it is possible to show skill",
        "Just play, you'll get it"
    )

    override fun create(users: List<Int>) = Match(
        gameName = "CastleRun",
        state = Match.State.ON_GOING,
        players = users,
        currPlayer = users.first(),
        currTurn = 1,
        info = objectMapper.valueToTree(Board(users[0].toString(), users[1].toString()))
    )

    override fun setup(match: Match, infoSetup: GameLogic.InfoSetup): GameLogic.UpdateInfo {
        TODO("Not necessary")
    }

    override fun doTurn(match: Match, infoTurn: GameLogic.InfoTurn): GameLogic.UpdateInfo {
        val position = objectMapper.treeToValue(infoTurn.info, Position::class.java)
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

    override fun matchPlayerView(match: Match, playerId: Int) = match
}
*/