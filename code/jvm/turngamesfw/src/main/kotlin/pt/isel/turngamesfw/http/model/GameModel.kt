package pt.isel.turngamesfw.http.model

import java.util.UUID

data class SetupInputModel(
    val matchId: UUID,
    val info: Any,
)

data class TurnInputModel(
    val matchId: UUID,
    val info: Any,
)

data class GameNameInputModel(
    val gameName: String
)