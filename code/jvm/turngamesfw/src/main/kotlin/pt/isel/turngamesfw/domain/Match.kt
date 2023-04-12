package pt.isel.turngamesfw.domain

import java.time.Instant
import java.util.UUID

data class Match (
    val id: UUID = UUID.randomUUID(),
    val gameName: String,
    val state: State,
    val players: List<Int>, /****/
    val currPlayer: Int,
    val currTurn: Int,
    val deadlineTurn: Instant?,
    val created: Instant = Instant.now(),
    val info: Any
) {
    enum class State {
        SETUP,
        ON_GOING,
        END,
    }
}