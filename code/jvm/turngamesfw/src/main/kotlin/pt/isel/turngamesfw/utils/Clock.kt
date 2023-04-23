package pt.isel.turngamesfw.utils

import org.springframework.stereotype.Component
import java.time.Instant

interface Clock {
    fun now(): Instant
}

object RealClock : Clock {
    // To only have second precision
    override fun now(): Instant = Instant.ofEpochSecond(Instant.now().epochSecond)
}
