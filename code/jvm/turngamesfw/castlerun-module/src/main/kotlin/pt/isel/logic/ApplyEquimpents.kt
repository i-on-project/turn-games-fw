package pt.isel.logic

import pt.isel.domain.Board
import pt.isel.domain.Equipment
import pt.isel.domain.Piece

fun applyEquipment(board: Board, piece: Piece) {
    return when(piece.equipment!!.type) {
        Equipment.Type.Sword -> useSword()
        Equipment.Type.Shield -> useShield()
        Equipment.Type.Axe -> useAxe()
        Equipment.Type.Banner -> useBanner()
        Equipment.Type.Boots -> useBoots()
        Equipment.Type.Horse -> useHorse()
    }
}

fun useSword() {
    TODO("Not yet implemented")
}

fun useShield() {
    TODO("Not yet implemented")
}

fun useAxe() {
    TODO("Not yet implemented")
}

fun useBanner() {
    TODO("Not yet implemented")
}

fun useBoots() {
    TODO("Not yet implemented")
}

fun useHorse() {
    TODO("Not yet implemented")
}
