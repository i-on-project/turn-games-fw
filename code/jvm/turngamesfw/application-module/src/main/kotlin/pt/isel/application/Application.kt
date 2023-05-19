package pt.isel.application

import pt.isel.tictactoe.TicTacToeGameLogic
import pt.isel.turngamesfw.gameProvider
import pt.isel.turngamesfw.runServer

fun main(args: Array<String>) {
    gameProvider.addGame("TicTacToe", TicTacToeGameLogic())
    runServer()
}