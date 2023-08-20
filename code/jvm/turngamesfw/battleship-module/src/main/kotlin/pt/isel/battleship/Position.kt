package pt.isel.battleship

/**
 * Represents a position in grid
 *
 * @param col Column in grid
 * @param row Row in grid
 */
data class Position(
    val col: Int,
    val row: Int,
) {
    init {
        require(col in 0 until GRID_COLUMNS && row in 0 until GRID_ROWS) {
            "Out-of-bounds! Grid columns = $GRID_COLUMNS, Grid rows = $GRID_ROWS"
        }
    }
}