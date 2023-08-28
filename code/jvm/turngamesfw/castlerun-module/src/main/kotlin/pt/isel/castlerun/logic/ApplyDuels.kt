package pt.isel.castlerun.logic

import pt.isel.castlerun.domain.*

fun applyDuel(board: Board, duel: Duel): Board {
    val winner = if (duel.duelDices.dice1 >= duel.duelDices.dice2) duel.ally else duel.enemy
    val loser = if (winner == duel.ally) duel.enemy else duel.ally

    return board.kill(loser)
    /*
    return when(duel.duelNumber) {
        1 -> board.kill(loser)
        2 -> board.setNewAlly(winner, 2)
        3 -> board.freezePiece(loser, 3)
        4 -> board.moveForward(winner, 16)
        5 -> board.convertPiece(loser, winner.owner)
        6 -> board.crownKing(winner)

        else -> throw IllegalArgumentException("Invalid dice value")
    }
    */
}

fun Board.kill(piece: Piece): Board {
    val newTile = Tile(piece.position, this.getTile(piece.position).type, null)
    val newTiles = this.tiles.map { if (it.coords != piece.position) it else newTile }
    val newPiecesLeft = this.piecesLeft.inc(piece.owner)

    return this.copy(tiles = newTiles, piecesLeft = newPiecesLeft)
}
/*
fun Board.setNewAlly(piece: Piece, spacesBehind: Int): Board {
    val newPos = this.getFirstAvailablePosition(piece.position, spacesBehind, pt.isel.castlerun.domain.Coords.Direction.Left)
    val newPiece = Piece(piece.owner, newPos)
    val newTile = Tile(piece.position, this.getTile(piece.position).type, newPiece)
    val b = this.updateTile(newTile)
    val newPiecesLeft = this.piecesLeft.dec(piece.owner)

    return b.copy(piecesLeft = newPiecesLeft)
}

private fun Board.freezePiece(piece: Piece, turns: Int): Board {
    val newPiece = piece.copy(frozen = turns)
    val newTile = Tile(piece.position, this.getTile(piece.position).type, newPiece)
    return this.updateTile(newTile)
}

private fun Board.moveForward(piece: Piece, distance: Int): Board {
    val newPos = this.getFirstAvailablePosition(piece.position, distance, pt.isel.castlerun.domain.Coords.Direction.Right)
    val newPiece = Piece(piece.owner, newPos)
    val newTileFrom = Tile(piece.position, this.getTile(piece.position).type, null)
    val newTileTo = Tile(newPos, this.getTile(newPos).type, newPiece)

    return this.updateTile(newTileFrom).updateTile(newTileTo)
}

private fun Board.convertPiece(piece: Piece, newOwner: Int): Board {
    val newPiece = Piece(newOwner, piece.position)
    val newTile = Tile(piece.position, this.getTile(piece.position).type, newPiece)
    
    return this.updateTile(newTile)
}

private fun Board.crownKing(piece: Piece): Board {
    val newPiece = piece.copy(isKing = true)
    val newTile = Tile(piece.position, this.getTile(piece.position).type, newPiece)
    return this.updateTile(newTile)
}*/
