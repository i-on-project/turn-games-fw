package pt.isel.castlerun

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import pt.isel.castlerun.domain.*
import pt.isel.castlerun.logic.applyDeploy
import pt.isel.fwinterfaces.*
import pt.isel.castlerun.logic.applyDuel
import pt.isel.castlerun.logic.applyMove
import pt.isel.castlerun.logic.newBoard
import java.lang.Error

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

        class TileDeserializer : JsonDeserializer<Tile>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Tile {
                val node: JsonNode = parser.codec.readTree(parser)
                val coords: Coords = node.get("coords").traverse(parser.codec).readValueAs(Coords::class.java)
                val type: Tile.Type = Tile.Type.valueOf(node.get("type").asText())
                val piece: Piece? =
                    if (node.get("piece").isNull)
                        null
                    else
                        objectMapper.treeToValue(node.get("piece"), Piece::class.java)

                return Tile(coords, type, piece)
            }
        }
        
        class PieceDeserializer : JsonDeserializer<Piece>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Piece {
                val node: JsonNode = parser.codec.readTree(parser)
                val owner: Int = node.get("owner").asInt()
                val position: Coords = objectMapper.treeToValue(node.get("position"), Coords::class.java)
                val frozen: Int = node.get("frozen").asInt()
                val isKing: Boolean = node.get("king").asBoolean()

                return Piece(owner, position, frozen, isKing)
            }
        }

        class PiecesLeftDeserializer : JsonDeserializer<PiecesLeft>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): PiecesLeft {
                val node: JsonNode = parser.codec.readTree(parser)
                val alpha: Int = node.get("alpha").asInt()
                val beta: Int = node.get("beta").asInt()
                val forAlpha: Int = node.get("forAlpha").asInt()
                val forBeta: Int = node.get("forBeta").asInt()

                return PiecesLeft(alpha, beta, forAlpha, forBeta)
            }
        }

        class DicesDeserializer : JsonDeserializer<Dices>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Dices {
                val node: JsonNode = parser.codec.readTree(parser)
                val dice1: Int = node.get("dice1").asInt()
                val dice2: Int = node.get("dice2").asInt()
                
                return Dices(dice1, dice2)
            }
        }

        class BoardDeserializer : JsonDeserializer<Board>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Board {
                val node: JsonNode = parser.codec.readTree(parser)

                val playerA: Int = node.get("alpha").asInt()
                val playerB: Int = node.get("beta").asInt()
                val numRows: Int = node.get("numRows").asInt()
                val numCols: Int = node.get("numCols").asInt()
                val numPieces: Int = node.get("numPieces").asInt()
                val piecesLeft: PiecesLeft = objectMapper.treeToValue(node.get("piecesLeft"), PiecesLeft::class.java)
                val tiles: List<Tile> = node.get("tiles").map { t -> objectMapper.treeToValue(t, Tile::class.java) }
                
                return Board(playerA, playerB, numRows, numCols, numPieces, piecesLeft, tiles)
            }
        }

        class TurnDeserializer : JsonDeserializer<Turn>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Turn {
                val node: JsonNode = parser.codec.readTree(parser)

                println(node)

                val type: String = node.get("type").asText()

                return when (type) {
                    "move" -> {
                        Turn(type, objectMapper.treeToValue(node.get("move"), Move::class.java), null)
                    }
                    "duel" -> {
                        Turn(type, null, objectMapper.treeToValue(node.get("duel"), Duel::class.java))
                    }
                    else -> error("Unrecognised type!")
                }
            }
        }

        class MoveDeserializer : JsonDeserializer<Move>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Move {
                val node: JsonNode = parser.codec.readTree(parser)

                val piece: Piece? = objectMapper.treeToValue(node.get("piece"), Piece::class.java)
                val to: Coords = objectMapper.treeToValue(node.get("to"), Coords::class.java)

                return Move(piece, to)
            }
        }

        class DuelDeserializer : JsonDeserializer<Duel>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Duel {
                val node: JsonNode = parser.codec.readTree(parser)

                val ally : Piece = objectMapper.treeToValue(node.get("ally"), Piece::class.java)
                val enemy : Piece = objectMapper.treeToValue(node.get("enemy"), Piece::class.java)
                val duelDices : Dices = objectMapper.treeToValue(node.get("duelDices"), Dices::class.java)
                val duelNumber : Int = node.get("duelNumber").asInt()

                return Duel(ally, enemy, duelDices, duelNumber)
            }
        }

        private val deserializerModule: SimpleModule =
            SimpleModule()
                .addDeserializer(Coords::class.java, CoordsDeserializer())
                .addDeserializer(Tile::class.java, TileDeserializer())
                .addDeserializer(Piece::class.java, PieceDeserializer())
                .addDeserializer(PiecesLeft::class.java, PiecesLeftDeserializer())
                .addDeserializer(Dices::class.java, DicesDeserializer())
                .addDeserializer(Board::class.java, BoardDeserializer())
                .addDeserializer(Turn::class.java, TurnDeserializer())
                .addDeserializer(Move::class.java, MoveDeserializer())
                .addDeserializer(Duel::class.java, DuelDeserializer())

        private val objectMapper: ObjectMapper = ObjectMapper().registerModule(deserializerModule)
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
        info = objectMapper.valueToTree(newBoard(users[0], users[1]))
    )

    override fun setup(match: Match, infoSetup: GameLogic.InfoSetup): GameLogic.UpdateInfo {
        TODO("Not necessary")
    }

    override fun doTurn(match: Match, infoTurn: GameLogic.InfoTurn): GameLogic.UpdateInfo {
        val board: Board = objectMapper.treeToValue(match.info, Board::class.java)
        val turn: Turn = objectMapper.treeToValue(infoTurn.info, Turn::class.java)

        val newBoard = try {
            if (turn.type == "move")
                if (turn.move!!.piece != null)
                    applyMove(board, turn.move)
                else
                    applyDeploy(board, infoTurn.playerId, turn.move.to)
            else
                applyDuel(board, turn.duel!!)
        } catch (e: Error) {
            return GameLogic.UpdateInfo(true, "Turn was qualified as invalid!", null)
        }

        val newMatch =
            if (newBoard.isGameOver())
                match.copy(
                    state = Match.State.FINISHED,
                    info = objectMapper.valueToTree(newBoard),
                )
            else
                match.copy(
                    currPlayer = match.players.find { it != match.currPlayer }!!,
                    currTurn = match.currTurn + 1,
                    info = objectMapper.valueToTree(newBoard),
                )

        return GameLogic.UpdateInfo(false, "Turn completed!", newMatch)
    }

    override fun matchPlayerView(match: Match, playerId: Int) = match
}