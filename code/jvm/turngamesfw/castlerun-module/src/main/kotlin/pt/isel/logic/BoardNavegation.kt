package pt.isel.logic

import pt.isel.domain.*

fun getNeighbours(board: Board, position: Coords): List<Coords> {
    val neighbours = mutableListOf<Coords>()

    val up = Coords(position.row - 1, position.col)
    val down = Coords(position.row + 1, position.col)
    val left = Coords(position.row, position.col - 1)
    val right = Coords(position.row, position.col + 1)

    if (up.row >= 0 && board.getTile(up).type != Tile.Type.Wall) { neighbours.add(up) }
    if (down.row < board.numRows && board.getTile(down).type != Tile.Type.Wall) { neighbours.add(down) }
    if (left.col >= 0 && board.getTile(left).type != Tile.Type.Wall) { neighbours.add(left) }
    if (right.col < board.numCols && board.getTile(right).type != Tile.Type.Wall) { neighbours.add(right) }

    return neighbours
}

fun possibleTiles(board: Board, start: Coords, distance: Int): MutableList<Coords> {
    val possibleTiles = mutableListOf<Coords>()
    val visited = mutableListOf<Pair<Coords, Int>>()
    val queue = mutableListOf<Pair<Coords, Int>>()

    queue.add(Pair(start, 0))

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        visited.add(current)

        if (current.second == distance || board.getTile(current.first).type == Tile.Type.Exit)
            possibleTiles.add(current.first)
        else
            getNeighbours(board, current.first).forEach {
                if (!visited.any { v -> v.first == it })
                    queue.add(Pair(it, current.second + 1))
            }
    }

    return possibleTiles
}

fun findPossibleMoves(board: Board, player: String, distance: Int): List<Pair<Piece?, Coords>> {
    val list = mutableListOf<Pair<Piece?, Coords>>()

    //Deploy
    val entrance = board.tiles.first{tile -> tile.type == if (player == board.playerA) Tile.Type.EntranceA else Tile.Type.EntranceB }.coords
    
    val deployTiles = possibleTiles(board, entrance, distance)
    deployTiles
        .filter { c -> filter(board.getTile(c), player) }
        .forEach { c -> list.add(Pair(null, c))}

    //Move
    val pieces = board.tiles.filter { t -> t.piece?.owner == player && t.type != Tile.Type.Exit }
    pieces.forEach { p ->
        val possibleCoords = possibleTiles(board, p.coords, distance)
        possibleCoords
            .filter { c -> filter(board.getTile(c), player) }
            .forEach { c -> list.add(Pair(p.piece, c))}
    }

    return list
}

private fun filter(tile: Tile, player: String): Boolean {
    return if (tile.piece == null)
        true
    else {
        if (tile.type == Tile.Type.Exit)
            return false
        if (tile.piece!!.owner == player)
            return false
        else
            return tile.piece!!.equipment != Equipment(Equipment.Type.Shield, true)
    }
}
