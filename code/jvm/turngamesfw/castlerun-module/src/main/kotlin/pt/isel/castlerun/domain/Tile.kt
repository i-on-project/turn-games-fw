package pt.isel.castlerun.domain

/**
 * Represents a tile on the game board.
 *
 * @property coords The coordinates of the tile on the game board.
 * @property type The type of the tile (default is Type.Floor).
 * @property piece The game piece located on the tile (default is null).
 */
data class Tile(val coords: Coords, var type: Type = Type.Floor, var piece: Piece? = null) {
    /**
     * The possible types of a tile.
     */
    enum class Type { Floor, Wall, Exit, EntranceA, EntranceB }

    /**
     * Returns a string representation of the tile.
     *
     * @return A string containing the coordinates and, if present, the associated game piece.
     */
    override fun toString() = "$coords ${if (piece == null) "with no piece" else "$piece"}"
}

/**
 * Represents coordinates on the game board.
 *
 * @property row The row of the coordinates.
 * @property col The column of the coordinates.
 */
data class Coords(val row: Int, val col: Int)
