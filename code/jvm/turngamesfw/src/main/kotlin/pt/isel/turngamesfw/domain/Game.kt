package pt.isel.turngamesfw.domain

data class Game(
    val name: String,
    val nunPlayers: Int,
    val description: String,
    val rules: String,
)