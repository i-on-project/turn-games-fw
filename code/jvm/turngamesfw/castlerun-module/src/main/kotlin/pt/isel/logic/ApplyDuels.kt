package pt.isel.logic

import pt.isel.domain.Board
import pt.isel.domain.Dices
import pt.isel.domain.Piece

data class Duel(val ally: Piece, val enemy: Piece, val duelDices: Dices)

fun applyDuel(board: Board, player: String, playDices: Dices, duel: Duel): Board {
    return when(playDices.dice1) {
        1 -> applyDuel1()
        2 -> applyDuel2()
        3 -> applyDuel3()
        4 -> applyDuel4()
        5 -> applyDuel5()
        6 -> applyDuel6()

        else -> throw IllegalArgumentException("Invalid dice value")
    }
}

fun applyDuel1(): Board {
    TODO("Not yet implemented")
}

fun applyDuel2(): Board {
    TODO("Not yet implemented")
}

fun applyDuel3(): Board {
    TODO("Not yet implemented")
}

fun applyDuel4(): Board {
    TODO("Not yet implemented")
}

fun applyDuel5(): Board {
    TODO("Not yet implemented")
}

fun applyDuel6(): Board {
    TODO("Not yet implemented")
}

