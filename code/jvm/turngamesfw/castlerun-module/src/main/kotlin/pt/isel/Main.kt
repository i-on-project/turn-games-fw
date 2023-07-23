package pt.isel

import pt.isel.domain.*
import pt.isel.logic.Move
import pt.isel.logic.applyDeploy
import pt.isel.logic.applyMove
import pt.isel.logic.findPossibleMoves

fun main() {
    var board = Board("A", "B")
    //Board.printBoard(board)
    var currentPlayer = "A"
    val playDices = Dices()
    val duelDices = Dices()

    while (board.isGameOver() == null) {
        print("Player $currentPlayer is rolling the dice... ")
        //First roll dices
        playDices.roll()
        println("${playDices.dice1} ${playDices.dice2}")

        //Is duel or move?
        if (playDices.areEqual()) {
            //Duel

        } else {
            //Move
            val moves = findPossibleMoves(board, currentPlayer, playDices.sum())
            println("Possible Tiles: ")
            moves.forEach {
                if (it.first == null)
                    println(it.second)
                else
                    println(it)
            }
            val move = moves.random()
            board = if (move.first == null) {
                println("Player $currentPlayer deployed a new piece at ${move.second}")
                applyDeploy(board, currentPlayer, move.second)
            } else {
                println("Player $currentPlayer moved a piece from ${move.first!!.position} to ${move.second}")
                applyMove(board, Move(move.first, move.second))
            }
        }

        //Is over?
        if (board.isGameOver() != null)
            println(board.isGameOver())

        currentPlayer = if(currentPlayer == "A") "B" else "A"

        println("--Press enter for next turn--")
        readln()
    }

    Board.printBoard(board)
}