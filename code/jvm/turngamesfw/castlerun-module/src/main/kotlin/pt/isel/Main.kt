package pt.isel

import pt.isel.domain.*
import pt.isel.logic.findPossibleMoves

fun main() {
    val board = Board("A", "B")
    //Board.printBoard(board)
    var currentPlayer = "A"

    while (board.isGameOver() == null) {
        //First roll dices
        board.playDices.roll()

        //Is duel or move?
        if (board.playDices.areEqual()) {
            //Duel

        } else {
            //Move
            val moves = findPossibleMoves(board, currentPlayer)
            val move = moves.random()
        }

        //Is over?
        if (board.isGameOver() != null)
            println(board.isGameOver())

        currentPlayer = if(currentPlayer == "A") "B" else "A"
    }

    Board.printBoard(board)
}