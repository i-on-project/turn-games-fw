package pt.isel.logic

import pt.isel.domain.Board
import pt.isel.domain.Coords
import pt.isel.domain.Piece
import pt.isel.domain.Tile

data class Move(val piece: Piece?, val to: Coords)


/**
 * First: Check if there are a pieces left
 * Second: Check there are no friendly pieces in @param to
 * Third: 
 * */
fun applyDeploy(board: Board, player: String, to: Coords): Board {
    var b = board

    if (board.getTile(to).piece != null)
        b = applyKill(board, board.getTile(to).piece!!)

    val newPiece = Piece(player, to)

    if (b.getTile(to).type == Tile.Type.Equipment)
        newPiece.equip()

    val newTile = Tile(to, b.getTile(to).type, newPiece)
    val newTiles = b.tiles.map { if (it.coords != to) it else newTile }

    val newPiecesLeft = b.piecesLeft.dec(player)

    return board.copy(tiles = newTiles, piecesLeft = newPiecesLeft)
}

fun applyMove(board: Board, move: Move): Board {
    require(move.piece != null)
    var b = board

    if (board.getTile(move.to).piece != null)
        b = applyKill(board, board.getTile(move.to).piece!!)

    /*
    if (move.piece.equipment != null)
        b = applyEquipment(board, move.piece, move.to)
    */

    val newPiece = move.piece.copy(position = move.to)

    val newTileFrom = Tile(move.piece.position, b.getTile(move.piece.position).type, null)
    val newTileTo = Tile(move.to, b.getTile(move.to).type, newPiece)

    val newTiles = b.tiles.map {
        when (it.coords) {
            move.to -> newTileTo
            move.piece.position -> newTileFrom
            else -> it
        }
    }

    return b.copy(tiles = newTiles)
}

fun applyKill(board: Board, piece: Piece): Board {
    val newTile = Tile(piece.position, board.getTile(piece.position).type, null)
    val newTiles = board.tiles.map { if (it.coords != piece.position) it else newTile }
    val newPiecesLeft = board.piecesLeft.inc(piece.owner)

    return board.copy(tiles = newTiles, piecesLeft = newPiecesLeft)
}