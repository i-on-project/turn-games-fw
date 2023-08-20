package pt.isel.battleship

/**
 * Represents the ship on grid
 *
 * @param type Type of ship
 * @param position Position of the ship in grid, position of lower col and lower row of ship
 * @param orientation Orientation of the ship on grid
 */
data class Ship(
    val type: Type,
    val position: Position,
    val orientation: Orientation
) {

    init {
        require(orientation == Orientation.VERTICAL && position.row <= GRID_ROWS - type.size || orientation == Orientation.HORIZONTAL && position.col <= GRID_COLUMNS - type.size) {
            "Position of ship $type is out-of-bounds"
        }
    }

    companion object {
        /**
         * Gets ship end position, from type size, orientation and initial position
         */
        fun getEndPosition(ship: Ship): Position {
            val rowIncrement = if (ship.orientation == Orientation.VERTICAL) ship.type.size - 1 else 0
            val colIncrement = if (ship.orientation == Orientation.HORIZONTAL) ship.type.size - 1 else 0

            return Position(ship.position.col + colIncrement, ship.position.row + rowIncrement)
        }
    }

    enum class Type(val size: Int) {
        CARRIER(5),
        BATTLESHIP(4),
        CRUISER(3),
        SUBMARINE(3),
        DESTROYER(2),
    }

    enum class Orientation {
        HORIZONTAL,
        VERTICAL,
    }
}