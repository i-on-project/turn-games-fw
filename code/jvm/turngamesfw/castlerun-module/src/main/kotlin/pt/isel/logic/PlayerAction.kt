package pt.isel.logic

import pt.isel.domain.*

data class Move(val piece: Piece, val to: Coords)

data class Duel(val ally: Piece, val enemy: Piece)

fun applyMove(board: Board, player: String, move: Move): Board {
    //Checks
    require(board.getTile(move.piece.position).piece == move.piece )
    require(move.piece.owner == player)

    return board
}

fun applyDuel(board: Board, player: String, duel: Duel): Board {
    //Checks
    require(board.getTile(duel.ally.position).piece == duel.ally )
    require(duel.ally.owner == player)
    require(board.getTile(duel.enemy.position).piece == duel.enemy )
    require(duel.enemy.owner != player)

    return board
}
