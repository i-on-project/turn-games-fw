package pt.isel.fwinterfaces

data class Game(
    val name: String,
    val numPlayers: Int,
    val description: String,
    val rules: String,
)