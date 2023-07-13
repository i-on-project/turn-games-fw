package pt.isel.domain

data class Piece(val owner: String, var position: Coords) {
    var equipment: Equipment? = null

    fun equip() {
        val e = Equipment.Type.values().random()

        equipment = if (equipment == null)
            Equipment(e, false)
        else if (equipment!!.upgraded)
            return
        else
            Equipment(e, equipment!!.type == e)
    }
}

data class Equipment(val type: Type, val upgraded: Boolean) {
    enum class Type { Shield, Sword, Axe, Banner, Boots, Horse }
}