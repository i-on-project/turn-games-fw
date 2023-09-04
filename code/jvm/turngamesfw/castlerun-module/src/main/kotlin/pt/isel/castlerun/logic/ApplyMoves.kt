package pt.isel.castlerun.logic

import pt.isel.castlerun.domain.*

fun applyDeploy(board: Board, player: Int, to: Coords): Board {
    var b = board

    if (board.getTile(to).piece != null)
        b = board.kill(board.getTile(to).piece!!)

    val newPiece = Piece(player, to)

    b = b.updateTile(Tile(to, b.getTile(to).type, newPiece))

    val newPiecesLeft = b.piecesLeft.dec(player)

    return b.copy(piecesLeft = newPiecesLeft)
}

fun applyMove(board: Board, move: Move): Board {
    require(move.piece != null)
    var b = board

    if (board.getTile(move.to).piece != null)
        b = board.kill(board.getTile(move.to).piece!!)

    val newPiece = move.piece.copy(position = move.to)

    val newTileFrom = Tile(move.piece.position, b.getTile(move.piece.position).type, null)
    val newTileTo = Tile(move.to, b.getTile(move.to).type, newPiece)

    return b.updateTile(newTileFrom).updateTile(newTileTo)
}
