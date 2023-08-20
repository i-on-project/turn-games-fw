package pt.isel.battleship

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import pt.isel.fwinterfaces.Game
import pt.isel.fwinterfaces.GameLogic
import pt.isel.fwinterfaces.Match

class BattleshipGameLogic: GameLogic {

    companion object {
        class PositionDeserializer : JsonDeserializer<Position>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Position {
                val node: JsonNode = parser.codec.readTree(parser)
                val col: Int = node.get("col").asInt()
                val row: Int = node.get("row").asInt()
                return Position(col, row)
            }
        }

        class LayoutShipsDeserializer : JsonDeserializer<LayoutShips>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): LayoutShips {
                val node: JsonNode = parser.codec.readTree(parser)
                val shipsNode: JsonNode = node
                val ships: List<Ship> = shipsNode.map { shipNode ->
                    val ship: Ship = objectMapper.treeToValue(shipNode, Ship::class.java)
                    ship
                }
                return LayoutShips(ships)
            }
        }

        class ShipDeserializer : JsonDeserializer<Ship>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Ship {
                val node: JsonNode = parser.codec.readTree(parser)
                val typeName: String = node.get("type").asText()
                val type: Ship.Type = Ship.Type.valueOf(typeName)
                val position: Position = objectMapper.treeToValue(node.get("position"), Position::class.java)
                val orientationName: String = node.get("orientation").asText()
                val orientation: Ship.Orientation = Ship.Orientation.valueOf(orientationName)
                return Ship(type, position, orientation)
            }
        }

        class BoardDeserializer : JsonDeserializer<Board>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): Board {
                val node: JsonNode = parser.codec.readTree(parser)

                val boardPlayer1: BoardPlayer = objectMapper.treeToValue(node.get("board1"), BoardPlayer::class.java)
                val boardPlayer2: BoardPlayer = objectMapper.treeToValue(node.get("board2"), BoardPlayer::class.java)

                return Board(boardPlayer1, boardPlayer2)
            }
        }

        class BoardPlayerDeserializer : JsonDeserializer<BoardPlayer>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): BoardPlayer {
                val node: JsonNode = parser.codec.readTree(parser)

                val boardShips: BoardShips = objectMapper.treeToValue(node.get("boardShips"), BoardShips::class.java)
                val boardShots: BoardShots = objectMapper.treeToValue(node.get("boardShots"), BoardShots::class.java)

                return BoardPlayer(boardShips, boardShots)
            }
        }

        class BoardShipsDeserializer : JsonDeserializer<BoardShips>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): BoardShips {
                val node: JsonNode = parser.codec.readTree(parser)

                val grid: Array<Array<Boolean>> = objectMapper.treeToValue(node.get("grid"), Array<Array<Boolean>>::class.java)
                val ships: List<Ship> = node.get("ships").map { shipNode ->
                    val ship: Ship = objectMapper.treeToValue(shipNode, Ship::class.java)
                    ship
                }
                val setupDone: Boolean = objectMapper.treeToValue(node.get("setupDone"), Boolean::class.java)

                return BoardShips(grid, ships, setupDone)
            }
        }

        class BoardShotsDeserializer : JsonDeserializer<BoardShots>() {
            override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): BoardShots {
                val node: JsonNode = parser.codec.readTree(parser)

                val grid: Array<Array<Boolean>> = objectMapper.treeToValue(node.get("grid"), Array<Array<Boolean>>::class.java)

                return BoardShots(grid)
            }
        }

        private val deserializerModule: SimpleModule =
            SimpleModule()
                .addDeserializer(Position::class.java, PositionDeserializer())
                .addDeserializer(Ship::class.java, ShipDeserializer())
                .addDeserializer(LayoutShips::class.java, LayoutShipsDeserializer())
                .addDeserializer(Board::class.java, BoardDeserializer())
                .addDeserializer(BoardPlayer::class.java, BoardPlayerDeserializer())
                .addDeserializer(BoardShips::class.java, BoardShipsDeserializer())
                .addDeserializer(BoardShots::class.java, BoardShotsDeserializer())

        private val objectMapper: ObjectMapper = ObjectMapper().registerModule(deserializerModule)
    }

    override fun getGameInfo() = Game(
        name = "Battleship",
        numPlayers = 2,
        description = "Battleship description",
        rules = "Battleship rules"
    )

    override fun create(users: List<Int>) = Match(
        gameName = "Battleship",
        state = Match.State.SETUP,
        players = users,
        currPlayer = users.first(),
        currTurn = 0,
        info = objectMapper.valueToTree(Board(BoardPlayer.create(), BoardPlayer.create()))
    )

    override fun setup(match: Match, infoSetup: GameLogic.InfoSetup): GameLogic.UpdateInfo {
        val layoutShips = objectMapper.treeToValue(infoSetup.info, LayoutShips::class.java)
        val board = objectMapper.treeToValue(match.info, Board::class.java)

        val boardPlayer = if (infoSetup.playerId == match.players.first()) board.board1 else board.board2
        if (boardPlayer.boardShips.setupDone) {
            return GameLogic.UpdateInfo(true, "Setup already done! Waiting for other player setup.", null)
        }

        if (layoutShips.ships.size != Ship.Type.values().size) {
            return GameLogic.UpdateInfo(true, "Not enough ships in layout!", null)
        }

        enumValues<Ship.Type>().forEach { type ->
            layoutShips.ships.onEach { ship ->
                if (type == ship.type) {
                    return@forEach
                }
            }
            return GameLogic.UpdateInfo(true, "Error in layout ships!", null)
        }

        var newBoardPlayer = boardPlayer
        layoutShips.ships.onEach { ship ->
            if (newBoardPlayer.canPlaceShip(ship)) {
                newBoardPlayer = newBoardPlayer.placeShip(ship)
            } else {
                return GameLogic.UpdateInfo(true, "Cant place ship! Position not available.", null)
            }
        }

        val newBoard = if (infoSetup.playerId == match.players.first())
            board.copy(board1 = newBoardPlayer)
        else
            board.copy(board2 = newBoardPlayer)

        var newMatch = match.copy(
            info = objectMapper.valueToTree(newBoard)
        )

        if (newBoard.board1.boardShips.setupDone && newBoard.board2.boardShips.setupDone) {
            newMatch = newMatch.copy(
                state = Match.State.ON_GOING,
                currTurn = 1,
            )
        }

        return GameLogic.UpdateInfo(
            false,
            "Setup Done.",
            newMatch
        )
    }

    override fun doTurn(match: Match, infoTurn: GameLogic.InfoTurn): GameLogic.UpdateInfo {
        val position = objectMapper.treeToValue(infoTurn.info, Position::class.java)
        val board = objectMapper.treeToValue(match.info, Board::class.java)

        val playerLogic = if (infoTurn.playerId == match.players.first()) PlayerLogic.PLAYER_1_LOGIC else PlayerLogic.PLAYER_2_LOGIC

        if (playerLogic.opponentBoardPlayer(board).haveShot(position)) {
            return GameLogic.UpdateInfo(true, "Already Shot!", null)
        }

        val newBoardShots = playerLogic.opponentBoardPlayer(board).makeShot(position)
        val newBoard = playerLogic.newBoard(board, newBoardShots)

        val hitResult = if (playerLogic.opponentBoardPlayer(board).haveShip(position)) {
            HitResult(true, playerLogic.opponentBoardPlayer(board).getShip(position)?.type)
        } else {
            HitResult(false)
        }

        if (playerLogic.opponentBoardPlayer(board).allShipsDestroyed()) {
            val newMatch = match.copy(
                info = objectMapper.valueToTree(newBoard),
                state = Match.State.FINISHED
            )
            return GameLogic.UpdateInfo(
                false,
                "You Won!",
                newMatch
            )
        }

        val newMatch = match.copy(
            info = objectMapper.valueToTree(newBoard),
            currTurn = match.currTurn + 1,
            currPlayer = match.players[playerLogic.nextPlayer]
        )
        return GameLogic.UpdateInfo(
            false,
            if (hitResult.hit) "You hit ${hitResult.shipType}!" else "You miss!",
            newMatch
        )
    }

    override fun matchPlayerView(match: Match, playerId: Int): Match {
        val board = objectMapper.treeToValue(match.info, Board::class.java)

        val newBoard = if (playerId == match.players.first())
            board.copy(
                board2 = BoardPlayer(BoardShips.create(), board.board2.boardShots)
            )
        else
            board.copy(
                board1 = BoardPlayer(BoardShips.create(), board.board1.boardShots)
            )

        return match.copy(
            info = objectMapper.valueToTree(newBoard)
        )
    }

    class PlayerLogic(
        val nextPlayer: Int,
        val opponentBoardPlayer: (board: Board) -> BoardPlayer,
        val newBoard: (board: Board, boardPlayer: BoardPlayer) -> Board,
    ) {
        companion object {
            val PLAYER_1_LOGIC = PlayerLogic(
                nextPlayer = 1,
                opponentBoardPlayer = { board -> board.board2 },
                newBoard = { board, boardPlayer -> board.copy(board2 = boardPlayer) },
            )
            val PLAYER_2_LOGIC = PlayerLogic(
                nextPlayer = 0,
                opponentBoardPlayer = { board -> board.board1},
                newBoard = { board, boardPlayer -> board.copy(board1 = boardPlayer) },
            )
        }
    }

    data class HitResult(
        val hit: Boolean,
        val shipType: Ship.Type? = null
    ) {
        init {
            if (hit)
                require(shipType != null) {
                    "Miss ship type! Hit shot but no ship type"
                }
        }
    }
}