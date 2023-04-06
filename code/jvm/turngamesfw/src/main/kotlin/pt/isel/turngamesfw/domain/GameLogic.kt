package pt.isel.turngamesfw.domain

interface GameLogic {

    fun create(users: List<Int>): Match

    fun setup(match: Match, infoSetup: Any): Match

    fun doTurn(match: Match, infoTurn: Any): Match

    fun isOver(match: Match): Boolean

}