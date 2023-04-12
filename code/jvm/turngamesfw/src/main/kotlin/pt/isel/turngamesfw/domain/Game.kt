package pt.isel.turngamesfw.domain

data class Game(
    val name: String,
    val numPlayers: Int,
    val description: String,
    val rules: String,
)