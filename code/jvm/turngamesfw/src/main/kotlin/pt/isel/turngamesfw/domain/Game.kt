package pt.isel.turngamesfw.domain

import java.time.Instant
import java.util.UUID

data class Game (
    val id: UUID,
    val state: State,
    val players: List<Int>,
    val currPlayer: Int,
    val currTurn: Int,
    val deadlineTurn: Instant?,
    val created: Instant,
    val info: Any
) {
    enum class State {
        SETUP,
        ON_GOING,
        END,
    }
}

