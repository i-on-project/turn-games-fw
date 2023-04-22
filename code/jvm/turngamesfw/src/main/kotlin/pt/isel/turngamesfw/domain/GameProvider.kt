package pt.isel.turngamesfw.domain

class GameProvider {

    private val gamesMap: MutableMap<String, GameLogic> = mutableMapOf()

    fun addGame(gameName: String, gameLogic: GameLogic) {
        if (gamesMap.containsKey(gameName)) {
            TODO("Game Already exist")
            return
        }

        gamesMap[gameName] = gameLogic
    }

    fun getGameLogic(gameName: String) = gamesMap[gameName]

    fun getAllGameLogic() = gamesMap.map { it.value }

}