package pt.isel.battleship

const val GRID_ROWS = 10
const val GRID_COLUMNS = 10

data class Board(
    val board1: BoardPlayer,
    val board2: BoardPlayer
)

data class BoardPlayer(
    val boardShips: BoardShips,
    val boardShots: BoardShots,
) {

    /**
     * Get Cell that represent ship
     *
     * @param position Position to get
     */
    fun getShip(position: Position) = boardShips.getShip(position)

    /**
     * Check if it's a ship on given position
     *
     * @param position Position to check
     */
    fun haveShip(position: Position) = boardShips.haveShip(position)

    /**
     * Check if all ships are destroyed
     */
    fun allShipsDestroyed() = boardShips.allShipsDestroyed(boardShots)

    /**
     * Return if it is possible to place a ship on wanted position,
     * to place a ship you need to have at least one cell distance in all directions from others ships
     *
     * @param ship Ship wanted to check if it's possible
     */
    fun canPlaceShip(ship: Ship) = boardShips.canPlaceShip(ship)

    /**
     * Place ship into grid
     *
     * @param ship Ship wanted to place
     */
    fun placeShip(ship: Ship) = this.copy(boardShips = boardShips.placeShip(ship))

    /**
     * Check if is a shot in given position
     *
     * @param position Position wanted to check
     */
    fun haveShot(position: Position) = boardShots.haveShot(position)

    /**
     * Insert shot into grid on wanted position
     *
     * @param position Position of the shot
     */
    fun makeShot(position: Position) = this.copy(boardShots = boardShots.makeShot(position))

    companion object {
        fun create() = BoardPlayer(BoardShips.create(), BoardShots.create())
    }

}