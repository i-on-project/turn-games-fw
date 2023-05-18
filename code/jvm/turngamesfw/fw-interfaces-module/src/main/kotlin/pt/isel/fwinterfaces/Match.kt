package pt.isel.fwinterfaces

import com.fasterxml.jackson.databind.JsonNode
import java.time.Instant
import java.util.UUID

data class Match (
    val id: UUID = UUID.randomUUID(),
    val gameName: String,
    val state: State = State.SETUP,
    val players: List<Int>,
    val currPlayer: Int = players.first(),
    val currTurn: Int = 0,
    val deadlineTurn: Instant? = null,
    val created: Instant = Instant.now(),
    val info: JsonNode
) {
    enum class State {
        SETUP,
        ON_GOING,
        FINISHED,
    }
}