package pt.isel.domain

data class PiecesLeft(val A: String, val B: String, val forA: Int, val forB:Int) {
    fun dec(player: String): PiecesLeft {
        if (player == A) return this.copy(forA = forA-1)
        if (player == B) return this.copy(forB = forB-1)
        else error("Player not recognized!")
    }

    fun inc(player: String): PiecesLeft {
        if (player == A) return this.copy(forA = forA+1)
        if (player == B) return this.copy(forB = forB+1)
        else error("Player not recognized!")
    }
}
data class Board(
    val playerA: String,
    val playerB: String,

    val numRows: Int,
    val numCols: Int,
    val numPieces: Int,

    val piecesLeft: PiecesLeft,

    val tiles: List<Tile>,
) {
    constructor(
        playerA: String,
        playerB: String,

        numRows: Int = 9,
        numCols: Int = 36,

        numPieces: Int = 6,

        walls: List<Coords> = DefaultLayout.walls,
        entranceA: Coords = DefaultLayout.entranceA,
        entranceB: Coords = DefaultLayout.entranceB,
        exits: List<Coords> = DefaultLayout.exits,
    ) : this(
        playerA,
        playerB,

        numRows,
        numCols,
        numPieces,

        PiecesLeft(playerA, playerB, numPieces, numPieces),
        createTiles(numRows, numCols, walls, exits, entranceA, entranceB)
    )

    companion object {
        private fun createTiles(numRows: Int, numCols: Int, walls: List<Coords>, exits: List<Coords>, entranceA: Coords, entranceB: Coords): List<Tile> {
            val tiles = Array(numRows) { row -> Array(numCols) { col -> Tile(Coords(row, col)) } }.flatten()

            tiles[entranceA.row * numCols + entranceA.col].type = Tile.Type.EntranceA
            tiles[entranceB.row * numCols + entranceB.col].type = Tile.Type.EntranceB

            walls.forEach { c -> tiles[c.row * numCols + c.col].type = Tile.Type.Wall }
            exits.forEach { c -> tiles[c.row * numCols + c.col].type = Tile.Type.Exit }

            repeat(numRows * numCols / 10) {
                val t = tiles.random()
                if (t.type == Tile.Type.Floor) t.type = Tile.Type.Equipment
            }
            return tiles
        }

        fun printBoard(board: Board) {
            val numRows = board.numRows
            val numCols = board.numCols

            for (row in 0 until numRows) {
                for (col in 0 until numCols) {
                    val tile = board.getTile(Coords(row, col))
                    val symbol = when (tile.type) {
                        Tile.Type.Wall -> "#"
                        Tile.Type.Exit -> "E"
                        Tile.Type.Floor -> " "
                        Tile.Type.Equipment -> "X"
                        Tile.Type.EntranceA -> "/"
                        Tile.Type.EntranceB -> "\\"
                    }
                    val playerSymbol = when {
                        tile.piece?.owner == board.playerA -> "A"
                        tile.piece?.owner == board.playerB -> "B"
                        else -> symbol
                    }
                    print(playerSymbol)
                }
                println() // Move to the next line after printing each row
            }
        }
    }

    fun getTile(c: Coords): Tile = tiles[c.row * numCols + c.col]

    private fun getExits() = tiles.filter { it.type == Tile.Type.Exit }

    fun isGameOver(): String? {
        val exits = getExits()
        val playerAScore = exits.filter { it.piece?.owner == playerA }.size
        val playerBScore = exits.filter { it.piece?.owner == playerB }.size
        
        if (playerAScore == exits.size) return playerA
        if (playerBScore == exits.size) return playerB
        return null
    }
}

object DefaultLayout {
    val walls = listOf(
        Coords(0, 1), Coords(1, 1), Coords(2, 1), Coords(3, 1), Coords(5, 1), Coords(6, 1), Coords(7, 1), Coords(8, 1),
        Coords(7, 2), Coords(8, 2),
        Coords(1, 3), Coords(2, 3), Coords(4, 3), Coords(5, 3), Coords(7, 3), Coords(8, 3),
        Coords(2, 4), Coords(4, 4), Coords(8, 4),
        Coords(0, 5), Coords(6, 5),
        Coords(0, 6), Coords(1, 6), Coords(3, 6), Coords(4, 6), Coords(5, 6), Coords(6, 6), Coords(7, 6),
        Coords(6, 7),
        Coords(1, 8), Coords(3, 8), Coords(4, 8), Coords(8, 8),
        Coords(1, 9), Coords(5, 9), Coords(6, 9), Coords(8, 9),
        Coords(1, 10), Coords(2, 10), Coords(3, 10), Coords(5, 10),
        Coords(3, 11), Coords(7, 11),
        Coords(0, 12), Coords(1, 12), Coords(3, 12), Coords(4, 12), Coords(6, 12), Coords(7, 12),
        Coords(3, 13), Coords(4, 13), Coords(6, 13),
        Coords(1, 14), Coords(8, 14),
        Coords(1, 15), Coords(2, 15), Coords(3, 15), Coords(4, 15), Coords(5, 15), Coords(6, 15), Coords(8, 15),
        Coords(8, 16),
        Coords(0, 17), Coords(1, 17), Coords(2, 17), Coords(3, 17), Coords(5, 17), Coords(6, 17), Coords(7, 17), Coords(8, 17),
        Coords(0, 18), Coords(1, 18), Coords(2, 18), Coords(3, 18), Coords(5, 18), Coords(6, 18), Coords(7, 18), Coords(8, 18),

        Coords(1, 20), Coords(2, 20), Coords(3, 20), Coords(5, 20), Coords(6, 20), Coords(7, 20),
        Coords(2, 21), Coords(3, 21), Coords(7, 21),
        Coords(0, 22), Coords(3, 22), Coords(5, 22),
        Coords(0, 23), Coords(1, 23), Coords(5, 23), Coords(6, 23), Coords(8, 23),
        Coords(3, 24), Coords(4, 24), Coords(8, 24),
        Coords(1, 25), Coords(3, 25), Coords(6, 25),
        Coords(1, 26), Coords(5, 26), Coords(6, 26), Coords(7, 26),
        Coords(3, 27),
        Coords(0, 28), Coords(2, 28), Coords(3, 28), Coords(4, 28), Coords(6, 28), Coords(7, 28), Coords(8, 28),
        Coords(0, 29), Coords(3, 29), Coords(4, 29), Coords(8, 29),
        Coords(0, 30), Coords(1, 30), Coords(3, 30), Coords(6, 30), Coords(8, 30),
        Coords(0, 31), Coords(5, 31), Coords(6, 31), Coords(8, 31),
        Coords(0, 32), Coords(2, 32), Coords(3, 32),
        Coords(3, 33), Coords(4, 33), Coords(5, 33), Coords(7, 33),
        Coords(1, 34), Coords(3, 34), Coords(7, 34),
        Coords(1, 35), Coords(3, 35), Coords(5, 35), Coords(7, 35)
    )

    val entranceA = Coords(0, 0)
    val entranceB = Coords(8, 0)

    val exits = listOf(
        Coords(0, 35),
        Coords(2, 35),
        Coords(4, 35),
        Coords(6, 35),
        Coords(8, 35)
    )
}
