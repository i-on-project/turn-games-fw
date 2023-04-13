package pt.isel.turngamesfw.domain

interface GameLogic {

    data class InfoSetup(
        val playerId: Int,
        val info: Any
    )

    data class InfoTurn(
        val playerId: Int,
        val info: Any
    )

    data class UpdateInfo(
        val error: Boolean,
        val message: String,
        val match: Match?
    )

    fun create(users: List<Int>): Match

    fun setup(match: Match, infoSetup: InfoSetup): UpdateInfo

    fun doTurn(match: Match, infoTurn: InfoTurn): UpdateInfo

    fun isOver(match: Match): Boolean

    /***
     * Return match view for specific player
     */
    fun matchPlayerView(match: Match, playerId: Int): Match

}