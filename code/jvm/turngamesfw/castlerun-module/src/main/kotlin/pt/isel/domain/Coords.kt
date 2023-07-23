package pt.isel.domain

data class Coords(val row: Int, val col: Int){
    override fun toString() = "{$row $col}"
}
