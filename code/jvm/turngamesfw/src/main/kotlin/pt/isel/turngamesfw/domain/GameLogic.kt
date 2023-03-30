package pt.isel.turngamesfw.domain

interface GameLogic {

    fun setup(match: Match): Match

    fun doTurn(match: Match): Match

    fun isGameOver(match: Match): Boolean

}