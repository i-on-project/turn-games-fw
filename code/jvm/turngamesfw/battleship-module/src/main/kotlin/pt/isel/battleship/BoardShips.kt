package pt.isel.battleship

import kotlin.math.max
import kotlin.math.min

data class BoardShips(
    val grid: Array<Array<Boolean>>,
    val ships: List<Ship>,
    val setupDone: Boolean = false
) {

    init {
        require(grid.size == GRID_ROWS && grid.all { it.size == GRID_COLUMNS }) {
            "Array with incorrect dimensions! Grid columns = $GRID_COLUMNS, Grid rows = $GRID_ROWS"
        }
    }

    fun getShip(position: Position): Ship? {
        ships.onEach { ship ->
            if (ship.position.col == position.col || ship.position.row == position.row) {
                val startPosition = ship.position
                val endPosition = Ship.getEndPosition(ship)
                for (row in startPosition.row .. endPosition.row)
                    for (col in startPosition.col .. endPosition.col)
                        if (col == position.col && row == position.row) {
                            return ship
                        }
            }
        }
        return null
    }

    /**
     * Check if it's a ship on given position
     *
     * @param position Position to check
     */
    fun haveShip(position: Position) = grid[position.row][position.col]

    /**
     * Check if all ships are destroyed given a BoardShots
     *
     * @param boardShots BoardShots containing all the shots made
     */
    fun allShipsDestroyed(boardShots: BoardShots): Boolean {
        grid.onEachIndexed { row, grid ->
            grid.onEachIndexed { col, cell ->
                if (cell)
                    if (!boardShots.haveShot(Position(col, row)))
                        return false
            }
        }
        return true
    }

    /**
     * Return if it is possible to place a ship on wanted position,
     * to place a ship you need to have at least one cell distance in all directions from others ships
     */
    fun canPlaceShip(ship: Ship): Boolean {
        val startPosition = ship.position
        val endPosition = Ship.getEndPosition(ship)

        val rowStart = max(0, startPosition.row - 1)
        val rowEnd = min(GRID_ROWS - 1, endPosition.row + 1)
        val colStart = max(0, startPosition.col - 1)
        val colEnd = min(GRID_COLUMNS - 1, endPosition.col+ 1)

        for (row in rowStart .. rowEnd) {
            for (col in colStart .. colEnd) {
                if (grid[row][col])
                    return false
            }
        }

        return true
    }

    /**
     * Place ship into grid
     */
    fun placeShip(ship: Ship) = BoardShips(
        grid.clone().also {
            val startPosition = ship.position
            val endPosition = Ship.getEndPosition(ship)
            for (row in startPosition.row .. endPosition.row)
                for (col in startPosition.col .. endPosition.col)
                    grid[row][col] = true
        }, ships.plus(ship), true
    )

    companion object {
        /**
         * Create empty board with only ocean
         */
        fun create() = BoardShips(
            Array(GRID_ROWS) {
                Array(GRID_COLUMNS) {
                    false
                }
            },
            emptyList()
        )


    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoardShips

        if (!grid.contentDeepEquals(other.grid)) return false

        return true
    }

    override fun hashCode(): Int {
        return grid.contentDeepHashCode()
    }

}