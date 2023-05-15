package pt.isel.tictactow.domain

data class Position(
    val col: Int,
    val row: Int,
) {
    init {
        require(col in 0..2 && row in 0..2)
    }
}