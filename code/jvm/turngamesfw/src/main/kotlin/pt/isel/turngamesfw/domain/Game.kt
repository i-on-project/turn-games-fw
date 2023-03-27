package pt.isel.turngamesfw.domain

import java.util.UUID

data class Game (
    val id: UUID,
    val state: State,
    val players: List<Int>,
    val info: Any
) {
    enum class State {
        SETUP,
        ON_GOING,
        END,
    }
}

