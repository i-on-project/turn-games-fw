package pt.isel.battleship

data class BoardShots(
    val grid: Array<Array<Boolean>>
) {

    init {
        require(grid.size == GRID_ROWS && grid.all { it.size == GRID_COLUMNS }) {
            "Array with incorrect dimensions! Grid columns = $GRID_COLUMNS, Grid rows = $GRID_ROWS"
        }
    }

    /**
     * Return have a shot in given position
     */
    fun haveShot(position: Position) = grid[position.row][position.col]

    /**
     * Insert shot into grid on wanted position
     */
    fun makeShot(position: Position) = BoardShots(
        grid.clone().also {
            grid[position.row][position.col] = true
        }
    )

    companion object {

        /**
         * Create empty board
         */
        fun create() = BoardShots(
            Array(GRID_ROWS) {
                Array(GRID_COLUMNS) {
                    false
                }
            }
        )

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BoardShots

        if (!grid.contentDeepEquals(other.grid)) return false

        return true
    }

    override fun hashCode(): Int {
        return grid.contentDeepHashCode()
    }
}