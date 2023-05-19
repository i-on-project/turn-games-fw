package pt.isel.application

import pt.isel.tictactow.TicTacTowGameLogic
import pt.isel.turngamesfw.gameProvider
import pt.isel.turngamesfw.runServer

fun main(args: Array<String>) {
    gameProvider.addGame("TicTacTow", TicTacTowGameLogic())
    runServer()
}