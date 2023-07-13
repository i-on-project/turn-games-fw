package pt.isel.domain

data class Tile(val coords: Coords) {
    var piece: Piece? = null
    var type: Type = Type.Floor

    enum class Type { Floor, Wall, Equipment, Exit }
}