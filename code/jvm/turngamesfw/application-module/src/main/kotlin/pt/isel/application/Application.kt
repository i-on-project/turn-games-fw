package pt.isel.application

import pt.isel.tictactoe.TicTacToeGameLogic
import pt.isel.battleship.BattleshipGameLogic
import pt.isel.turngamesfw.Framework

fun main(args: Array<String>) {
    val fw = Framework()
    fw.setDbUrl("jdbc:postgresql://localhost:5432/dbTurnGamesFW?user=dbuser&password=12345")
    fw.addGameLogic("TicTacToe", TicTacToeGameLogic())
    fw.addGameLogic("BattleShip", BattleshipGameLogic())
    fw.runServer()
}