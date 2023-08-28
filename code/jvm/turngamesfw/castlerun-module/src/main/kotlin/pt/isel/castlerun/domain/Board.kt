package pt.isel.castlerun.domain

/**
 * Represents the game board that holds the state of the game.
 *
 * @property alpha The ID of player A.
 * @property beta The ID of player B.
 * @property numRows The number of rows on the board.
 * @property numCols The number of columns on the board.
 * @property numPieces The total number of game pieces.
 * @property piecesLeft The remaining pieces for each player.
 * @property tiles The list of tiles comprising the game board.
 */
data class Board(
    val alpha: Int,
    val beta: Int,
    val numRows: Int,
    val numCols: Int,
    val numPieces: Int,
    val piecesLeft: PiecesLeft,
    val tiles: List<Tile>,
) {
    /**
     * Retrieves the tile at the specified coordinates.
     *
     * @param c The coordinates of the tile to retrieve.
     * @return The tile at the given coordinates.
     */
    fun getTile(c: Coords): Tile = tiles[c.row * numCols + c.col]

    /**
     * Checks if a given set of coordinates is within the borders of the board.
     *
     * @param c The coordinates to check.
     * @return `true` if the coordinates are within the board's borders, `false` otherwise.
     */
    fun isWithinBorders(c: Coords): Boolean = c.row in 0 until numRows && c.col in 0 until numCols

    /**
     * Updates a tile on the board with a new tile.
     *
     * @param newTile The new tile to replace the existing tile.
     * @return A new instance of the board with the updated tile.
     */
    fun updateTile(newTile: Tile): Board =
        this.copy(tiles = tiles.map { if (it.coords == newTile.coords) newTile else it })

    /**
     * Checks if the game is over by evaluating the score of each player based on their exits.
     *
     * @return `true` if either player A or player B has reached the maximum score possible, `false` otherwise.
     */
    fun isGameOver(): Boolean {
        val exits = getExits()
        val playerAScore = exits.filter { it.piece?.owner == alpha }.size
        val playerBScore = exits.filter { it.piece?.owner == beta }.size

        return playerAScore == exits.size || playerBScore == exits.size
    }

    private fun getExits() = tiles.filter { it.type == Tile.Type.Exit }
}
