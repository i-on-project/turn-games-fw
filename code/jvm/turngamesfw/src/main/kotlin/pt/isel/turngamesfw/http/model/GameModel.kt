package pt.isel.turngamesfw.http.model

import java.util.UUID

data class SetupInputModel(
    val gameId: UUID,
    val info: Any,
)

data class TurnInputModel(
    val gameId: UUID,
    val info: Any,
)