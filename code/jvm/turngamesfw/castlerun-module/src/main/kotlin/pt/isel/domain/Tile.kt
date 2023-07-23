package pt.isel.domain

data class Tile(val coords: Coords, var type: Type = Type.Floor, var piece: Piece? = null) {
    enum class Type { Floor, Wall, Equipment, Exit, EntranceA, EntranceB }
    override fun toString() = "$coords ${if (piece == null) "with no piece" else "$piece"}"
}