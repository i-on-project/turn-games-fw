package pt.isel.turngamesfw.utils

import java.time.Instant

interface Clock {
    fun now(): Instant
}