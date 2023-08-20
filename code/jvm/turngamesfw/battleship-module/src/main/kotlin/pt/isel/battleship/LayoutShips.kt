package pt.isel.battleship

/**
 * Represents grid layout of all ships
 *
 * @param ships List with all Ships with the information of is position on grid
 */
data class LayoutShips(
    val ships: List<Ship>
)