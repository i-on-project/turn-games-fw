package pt.isel.castlerun.domain

/**
 * Represents a game piece on the board.
 *
 * @property owner The ID of the player who owns the piece.
 * @property position The position of the piece on the game board.
 * @property frozen The number of turns the piece is frozen for (default is 0).
 * @property king Indicates whether the piece is a king (default is false).
 */
data class Piece(
    val owner: Int,
    val position: Coords,
    val frozen: Int = 0,
    val king: Boolean = false
)

/**
 * Represents the count of remaining pieces for each player.
 *
 * @property alpha The ID of player Alpha.
 * @property beta The ID of player Beta.
 * @property forAlpha The count of pieces left for player Alpha.
 * @property forBeta The count of pieces left for player Beta.
 */
data class PiecesLeft(val alpha: Int, val beta: Int, val forAlpha: Int, val forBeta: Int) {
    /**
     * Decreases the count of remaining pieces for the specified player.
     *
     * @param player The ID of the player whose pieces count will be decreased.
     * @return A new instance of `PiecesLeft` with the appropriate count decreased.
     * @throws IllegalArgumentException If the provided player ID is not recognized.
     */
    fun dec(player: Int): PiecesLeft {
        if (player == alpha) return this.copy(forAlpha = forAlpha - 1)
        if (player == beta) return this.copy(forBeta = forBeta - 1)
        else throw IllegalArgumentException("Player not recognized!")
    }

    /**
     * Increases the count of remaining pieces for the specified player.
     *
     * @param player The ID of the player whose pieces count will be increased.
     * @return A new instance of `PiecesLeft` with the appropriate count increased.
     * @throws IllegalArgumentException If the provided player ID is not recognized.
     */
    fun inc(player: Int): PiecesLeft {
        if (player == alpha) return this.copy(forAlpha = forAlpha + 1)
        if (player == beta) return this.copy(forBeta = forBeta + 1)
        else throw IllegalArgumentException("Player not recognized!")
    }
}
